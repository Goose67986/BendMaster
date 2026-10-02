package com.bendmasterpro.app;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    LinearLayout root, body;
    final String[] names={"5052-H32 Aluminum","6061-T6 Aluminum","A572 Grade 42","A572 Grade 50","Cold Rolled Steel","HRPO A36","304 Stainless","316 Stainless"};
    final String[] cats={"aluminum","aluminum","steel","steel","steel","steel","stainless","stainless"};
    final double[] k={0.33,0.33,0.44,0.44,0.44,0.44,0.45,0.45};
    final double[] tensile={33000,45000,60000,65000,55000,58000,75000,75000};
    EditText thick, die, punch, angle, length, f1, f2, bends;
    Spinner mat; TextView out; boolean metric=false;

    @Override public void onCreate(Bundle b){super.onCreate(b); showCalculator();}

    TextView title(String s,int sp){ TextView v=new TextView(this);v.setText(s);v.setTextSize(sp);v.setPadding(12,14,12,10);return v;}
    EditText num(String hint){ EditText e=new EditText(this);e.setHint(hint);e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);e.setPadding(12,8,12,8);body.addView(e);return e;}
    Button btn(String s){Button b=new Button(this);b.setText(s);return b;}
    void shell(String heading){
        ScrollView scroll=new ScrollView(this); root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);scroll.addView(root);
        LinearLayout nav=new LinearLayout(this);nav.setOrientation(LinearLayout.HORIZONTAL);
        String[] ns={"Calculator","Flat Pattern","Materials","FAQ"};
        for(String x:ns){Button b=btn(x);nav.addView(b,new LinearLayout.LayoutParams(0,-2,1));b.setOnClickListener(v->{if(x.equals("Calculator"))showCalculator();else if(x.equals("Flat Pattern"))showFlat();else if(x.equals("Materials"))showMaterials();else showFaq();});}
        root.addView(nav);root.addView(title(heading,26));body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(18,4,18,28);root.addView(body);setContentView(scroll);
    }
    void showCalculator(){
        shell("BendMaster Pro");
        mat=new Spinner(this);mat.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,names));body.addView(mat);
        thick=num("Material thickness (in)");die=num("V-die opening (in)");punch=num("Punch radius (in)");angle=num("Bend angle (degrees)");length=num("Bend length (in)");
        angle.setText("90");
        Button units=btn("Switch inch / mm");body.addView(units);units.setOnClickListener(v->{metric=!metric;units.setText(metric?"Units: mm (tap for inch)":"Units: inch (tap for mm)");});
        Button calc=btn("Calculate");body.addView(calc);out=title("Enter values and calculate.",18);body.addView(out);
        calc.setOnClickListener(v->calculate());
    }
    double val(EditText e){try{return Double.parseDouble(e.getText().toString());}catch(Exception x){return Double.NaN;}}
    void calculate(){
        double t=val(thick),v=val(die),r=val(punch),a=val(angle),bl=val(length); if(metric){t/=25.4;v/=25.4;r/=25.4;bl/=25.4;}
        if(!(t>0&&v>0&&r>=0&&a>0&&a<180&&bl>0)){out.setText("Check inputs. Thickness, die, angle and bend length must be positive.");return;}
        int i=mat.getSelectedItemPosition(); double eff=Math.max(r,v/6.0); double rad=Math.toRadians(a);
        double ba=rad*(eff+k[i]*t); double ossb=Math.tan(rad/2.0)*(eff+t); double bd=2*ossb-ba;
        double tonsFt=1.33*tensile[i]*t*t/v/2000.0; double total=tonsFt*(bl/12.0); double rec=total*1.15;
        double flange=v/2.0+bd/2.0; double suggested=(cats[i].equals("aluminum")?4.5:3.0)*t;
        String u=metric?" mm":" in"; double cv=metric?25.4:1;
        out.setText(String.format(Locale.US,"Effective inside radius: %.3f%s\nBend allowance: %.3f%s\nBend deduction: %.3f%s\n\nTonnage: %.2f tons/ft\nTotal tonnage: %.2f tons\nRecommended (+15%%): %.2f tons\n\nShortest flange: %.3f%s\nRule-of-thumb die: %.3f%s",eff*cv,u,ba*cv,u,bd*cv,u,tonsFt,total,rec,flange*cv,u,suggested*cv,u));
    }
    void showFlat(){
        shell("Flat Pattern");
        f1=num("Flange 1 length");f2=num("Flange 2 length");bends=num("Number of bends");thick=num("Thickness");die=num("V-die opening");punch=num("Punch radius");angle=num("Bend angle");angle.setText("90");bends.setText("1");
        mat=new Spinner(this);mat.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,names));body.addView(mat);
        Button b=btn("Calculate flat");body.addView(b);out=title("",18);body.addView(out);
        b.setOnClickListener(v->{double a1=val(f1),a2=val(f2),n=val(bends),t=val(thick),d=val(die),r=val(punch),ang=val(angle);if(!(a1>0&&a2>0&&n>=1&&t>0&&d>0)){out.setText("Check inputs.");return;}int i=mat.getSelectedItemPosition();double er=Math.max(r,d/6),rr=Math.toRadians(ang),ba=rr*(er+k[i]*t),bd=2*Math.tan(rr/2)*(er+t)-ba,flat=a1+a2-n*bd;out.setText(String.format(Locale.US,"Bend deduction: %.3f\nDeveloped flat length: %.3f",bd,flat));});
    }
    void showMaterials(){shell("Material Library");for(int i=0;i<names.length;i++)body.addView(title(names[i]+"\nK-factor: "+k[i]+"   Tensile: "+String.format(Locale.US,"%.0f psi",tensile[i]),18));body.addView(title("v0.1 uses local material data. Values remain editable in later builds.",14));}
    void showFaq(){shell("Fabrication FAQ / Glossary");String s="K-factor — neutral-axis location through material thickness.\n\nBend allowance — arc length of the neutral axis through the bend.\n\nBend deduction — amount subtracted from outside flange dimensions to obtain developed flat length.\n\nV-die opening — die width used for air bending.\n\nTonnage — estimated forming force; always verify machine and tooling capacities.";body.addView(title(s,18));}
}

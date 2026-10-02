package com.bendmasterpro.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    final int BG=Color.rgb(14,17,21), CARD=Color.rgb(27,32,39), FIELD=Color.rgb(35,41,49);
    final int TEXT=Color.rgb(239,243,247), MUTED=Color.rgb(166,177,188), ACCENT=Color.rgb(43,166,255);
    LinearLayout page, content, results;
    final String[] names={"5052-H32 Aluminum","6061-T6 Aluminum","A572 Grade 42","A572 Grade 50","Cold Rolled Steel","HRPO A36","304 Stainless","316 Stainless"};
    final String[] cats={"aluminum","aluminum","steel","steel","steel","steel","stainless","stainless"};
    // TEST BUILD: material values will be verified against Base44 before production.
    final double[] k={0.33,0.33,0.44,0.44,0.44,0.44,0.45,0.45};
    final double[] tensile={33000,45000,60000,65000,55000,58000,75000,75000};
    EditText thick, die, punch, angle, length, f1, f2, bends;
    Spinner mat; boolean metric=false; Button unitIn,unitMm;

    int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    TextView text(String s,float sp,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(sp);v.setTextColor(color);return v;}
    GradientDrawable bg(int color,float radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp((int)radius));return g;}
    void margin(View v,int l,int t,int r,int b){ViewGroup.MarginLayoutParams p=(ViewGroup.MarginLayoutParams)v.getLayoutParams();p.setMargins(dp(l),dp(t),dp(r),dp(b));v.setLayoutParams(p);}

    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);showCalculator();}

    void shell(String title,String subtitle,int active){
        LinearLayout outer=new LinearLayout(this);outer.setOrientation(LinearLayout.VERTICAL);outer.setBackgroundColor(BG);
        ViewCompat.setOnApplyWindowInsetsListener(outer,(v,insets)->{Insets s=insets.getInsets(WindowInsetsCompat.Type.systemBars());v.setPadding(0,s.top,0,s.bottom);return insets;});

        LinearLayout head=new LinearLayout(this);head.setOrientation(LinearLayout.VERTICAL);head.setPadding(dp(20),dp(14),dp(20),dp(12));
        TextView brand=text(title,28,TEXT);brand.setTypeface(Typeface.DEFAULT,Typeface.BOLD);head.addView(brand);
        TextView sub=text(subtitle,13,MUTED);head.addView(sub);outer.addView(head);

        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        page=new LinearLayout(this);page.setOrientation(LinearLayout.VERTICAL);page.setPadding(dp(16),dp(4),dp(16),dp(24));scroll.addView(page);
        outer.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        LinearLayout nav=new LinearLayout(this);nav.setPadding(dp(8),dp(7),dp(8),dp(7));nav.setGravity(Gravity.CENTER);
        String[] labels={"CALC","FLAT","MATERIALS","FAQ"};
        for(int i=0;i<labels.length;i++){final int x=i;Button b=new Button(this);b.setText(labels[i]);b.setTextSize(11);b.setAllCaps(false);b.setTextColor(i==active?Color.WHITE:MUTED);b.setBackground(bg(i==active?ACCENT:Color.TRANSPARENT,12));b.setPadding(0,0,0,0);nav.addView(b,new LinearLayout.LayoutParams(0,dp(48),1));b.setOnClickListener(v->{if(x==0)showCalculator();else if(x==1)showFlat();else if(x==2)showMaterials();else showFaq();});}
        outer.addView(nav);setContentView(outer);
    }

    TextView section(String s){TextView v=text(s,13,MUTED);v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);v.setAllCaps(true);page.addView(v,new LinearLayout.LayoutParams(-1,-2));margin(v,4,16,4,8);return v;}
    LinearLayout card(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(16),dp(14),dp(16),dp(14));c.setBackground(bg(CARD,16));page.addView(c,new LinearLayout.LayoutParams(-1,-2));margin(c,0,0,0,12);return c;}
    EditText field(LinearLayout parent,String label,String value){
        TextView l=text(label,13,MUTED);parent.addView(l);margin(l,2,8,2,3);
        EditText e=new EditText(this);e.setText(value);e.setTextColor(TEXT);e.setHintTextColor(Color.rgb(105,116,127));e.setTextSize(18);e.setSingleLine(true);e.setPadding(dp(12),0,dp(12),0);e.setBackground(bg(FIELD,10));e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);parent.addView(e,new LinearLayout.LayoutParams(-1,dp(52)));margin(e,0,0,0,6);return e;
    }
    Spinner material(LinearLayout parent){
        TextView l=text("Material",13,MUTED);parent.addView(l);margin(l,2,4,2,3);
        Spinner s=new Spinner(this);ArrayAdapter<String>a=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,names){@Override public View getView(int p,View v,ViewGroup g){TextView t=(TextView)super.getView(p,v,g);t.setTextColor(TEXT);t.setTextSize(17);t.setPadding(dp(12),0,dp(12),0);return t;}};s.setAdapter(a);s.setBackground(bg(FIELD,10));parent.addView(s,new LinearLayout.LayoutParams(-1,dp(54)));return s;
    }
    Button action(String label){Button b=new Button(this);b.setText(label);b.setTextSize(16);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setTextColor(Color.WHITE);b.setBackground(bg(ACCENT,12));page.addView(b,new LinearLayout.LayoutParams(-1,dp(56)));margin(b,0,4,0,12);return b;}
    void units(){
        LinearLayout row=new LinearLayout(this);row.setPadding(dp(3),dp(3),dp(3),dp(3));row.setBackground(bg(FIELD,12));page.addView(row,new LinearLayout.LayoutParams(-1,dp(50)));margin(row,0,0,0,12);
        unitIn=new Button(this);unitMm=new Button(this);unitIn.setText("INCH");unitMm.setText("MM");for(Button b:new Button[]{unitIn,unitMm}){b.setTextSize(13);b.setAllCaps(false);b.setTextColor(TEXT);row.addView(b,new LinearLayout.LayoutParams(0,-1,1));}
        unitIn.setOnClickListener(v->{metric=false;paintUnits();});unitMm.setOnClickListener(v->{metric=true;paintUnits();});paintUnits();
    }
    void paintUnits(){unitIn.setBackground(bg(!metric?ACCENT:Color.TRANSPARENT,10));unitMm.setBackground(bg(metric?ACCENT:Color.TRANSPARENT,10));}
    double val(EditText e){try{return Double.parseDouble(e.getText().toString());}catch(Exception x){return Double.NaN;}}

    void showCalculator(){
        shell("BendMaster Pro","Press brake setup calculator • v0.2 test",0);
        section("Setup");LinearLayout c=card();mat=material(c);thick=field(c,"Material thickness","");die=field(c,"V-die opening","");punch=field(c,"Punch radius","");angle=field(c,"Bend angle (degrees)","90");length=field(c,"Bend length","");
        section("Units");units();Button calc=action("CALCULATE");results=new LinearLayout(this);results.setOrientation(LinearLayout.VERTICAL);page.addView(results);calc.setOnClickListener(v->calculate());
    }
    void resultCard(String heading,String body){
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(16),dp(14),dp(16),dp(14));c.setBackground(bg(CARD,16));results.addView(c,new LinearLayout.LayoutParams(-1,-2));margin(c,0,0,0,10);
        TextView h=text(heading,13,ACCENT);h.setTypeface(Typeface.DEFAULT,Typeface.BOLD);c.addView(h);TextView b=text(body,17,TEXT);b.setLineSpacing(0,1.25f);c.addView(b);margin(b,0,7,0,0);
    }
    void calculate(){
        results.removeAllViews();double t=val(thick),v=val(die),r=val(punch),a=val(angle),bl=val(length);if(metric){t/=25.4;v/=25.4;r/=25.4;bl/=25.4;}
        if(!(t>0&&v>0&&r>=0&&a>0&&a<180&&bl>0)){resultCard("CHECK INPUTS","Thickness, die opening, punch radius, angle and bend length are required.");return;}
        int i=mat.getSelectedItemPosition();double eff=Math.max(r,v/6.0),rad=Math.toRadians(a),ba=rad*(eff+k[i]*t),ossb=Math.tan(rad/2)*(eff+t),bd=2*ossb-ba;
        double tonsFt=1.33*tensile[i]*t*t/v/2000.0,total=tonsFt*(bl/12.0),rec=total*1.15,flange=v/2+bd/2,suggested=(cats[i].equals("aluminum")?4.5:3.0)*t,cv=metric?25.4:1;String u=metric?" mm":" in";
        resultCard("BEND GEOMETRY",String.format(Locale.US,"Inside radius   %.3f%s\nBend allowance   %.3f%s\nBend deduction   %.3f%s",eff*cv,u,ba*cv,u,bd*cv,u));
        resultCard("TONNAGE",String.format(Locale.US,"Per foot   %.2f tons/ft\nTotal   %.2f tons\nRecommended (+15%%)   %.2f tons",tonsFt,total,rec));
        resultCard("TOOLING",String.format(Locale.US,"Shortest flange   %.3f%s\nRule-of-thumb die   %.3f%s",flange*cv,u,suggested*cv,u));
    }

    void showFlat(){
        shell("Flat Pattern","Developed length calculator • v0.2 test",1);section("Part");LinearLayout c=card();mat=material(c);f1=field(c,"Flange 1 length","");f2=field(c,"Flange 2 length","");bends=field(c,"Number of bends","1");thick=field(c,"Material thickness","");die=field(c,"V-die opening","");punch=field(c,"Punch radius","");angle=field(c,"Bend angle (degrees)","90");Button b=action("CALCULATE FLAT");results=new LinearLayout(this);results.setOrientation(LinearLayout.VERTICAL);page.addView(results);
        b.setOnClickListener(v->{results.removeAllViews();double a1=val(f1),a2=val(f2),n=val(bends),t=val(thick),d=val(die),r=val(punch),ang=val(angle);if(!(a1>0&&a2>0&&n>=1&&t>0&&d>0&&r>=0&&ang>0)){resultCard("CHECK INPUTS","Complete all required dimensions.");return;}int i=mat.getSelectedItemPosition();double er=Math.max(r,d/6),rr=Math.toRadians(ang),ba=rr*(er+k[i]*t),bd=2*Math.tan(rr/2)*(er+t)-ba,flat=a1+a2-n*bd;resultCard("FLAT PATTERN",String.format(Locale.US,"Bend deduction   %.3f\nDeveloped length   %.3f",bd,flat));});
    }
    void showMaterials(){
        shell("Materials","Local reference library • test values",2);section("Built-in materials");for(int i=0;i<names.length;i++){LinearLayout c=card();TextView n=text(names[i],18,TEXT);n.setTypeface(Typeface.DEFAULT,Typeface.BOLD);c.addView(n);c.addView(text(String.format(Locale.US,"K-factor  %.2f     Tensile  %,.0f psi",k[i],tensile[i]),14,MUTED));}TextView w=text("Material properties in this test build must be verified before production use.",13,MUTED);page.addView(w);margin(w,4,4,4,12);
    }
    void showFaq(){
        shell("Fabrication FAQ","Quick press-brake reference",3);String[][] q={{"K-factor","Neutral-axis location through material thickness."},{"Bend allowance","Arc length of the neutral axis through the bend."},{"Bend deduction","Amount subtracted from outside flange dimensions to obtain developed flat length."},{"V-die opening","Die width used for air bending."},{"Tonnage","Estimated forming force. Always verify machine and tooling capacities."}};for(String[] x:q){LinearLayout c=card();TextView h=text(x[0],17,TEXT);h.setTypeface(Typeface.DEFAULT,Typeface.BOLD);c.addView(h);TextView b=text(x[1],14,MUTED);c.addView(b);margin(b,0,5,0,0);}
    }
}
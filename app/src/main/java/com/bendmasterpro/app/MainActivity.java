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
    final int BG=Color.rgb(246,248,251), CARD=Color.WHITE, TEXT=Color.rgb(31,41,55), MUTED=Color.rgb(107,114,128), BLUE=Color.rgb(37,99,235), BORDER=Color.rgb(226,232,240);
    LinearLayout page,results; Spinner mat; EditText thick,die,punch,angle,length,f1,f2,bends; Button unitIn,unitMm;
    boolean metric=false;
    int sharedMat=4; String sharedT="",sharedD="",sharedR="",sharedA="90",sharedL="";
    final String[] names={"5052-H32 Aluminum","6061-T6 Aluminum","A572 Grade 42","A572 Grade 50","Cold Rolled Steel","HRPO A36","304 Stainless","316 Stainless"};
    final String[] cats={"aluminum","aluminum","steel","steel","steel","steel","stainless","stainless"};
    final double[] k={0.41,0.41,0.44,0.44,0.44,0.44,0.43,0.43};
    final double[] tensile={33000,45000,60000,65000,55000,58000,75000,85000};

    int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    GradientDrawable box(int color,int stroke,int radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));if(stroke!=Color.TRANSPARENT)g.setStroke(dp(1),stroke);return g;}
    TextView tv(String s,float z,int c){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(c);return v;}
    void margins(View v,int l,int t,int r,int b){ViewGroup.MarginLayoutParams p=(ViewGroup.MarginLayoutParams)v.getLayoutParams();p.setMargins(dp(l),dp(t),dp(r),dp(b));v.setLayoutParams(p);}
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);showCalculator();}

    void shell(String heading,String sub,int active){
        LinearLayout outer=new LinearLayout(this);outer.setOrientation(LinearLayout.VERTICAL);outer.setBackgroundColor(BG);
        ViewCompat.setOnApplyWindowInsetsListener(outer,(v,i)->{Insets x=i.getInsets(WindowInsetsCompat.Type.systemBars());v.setPadding(0,x.top,0,x.bottom);return i;});
        LinearLayout h=new LinearLayout(this);h.setOrientation(LinearLayout.VERTICAL);h.setPadding(dp(20),dp(16),dp(20),dp(12));
        TextView logo=tv("BendMaster Pro",26,TEXT);logo.setTypeface(Typeface.DEFAULT,Typeface.BOLD);h.addView(logo);
        TextView st=tv(sub,13,MUTED);h.addView(st);outer.addView(h);
        ScrollView sv=new ScrollView(this);page=new LinearLayout(this);page.setOrientation(LinearLayout.VERTICAL);page.setPadding(dp(16),dp(4),dp(16),dp(28));sv.addView(page);outer.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav=new LinearLayout(this);nav.setBackgroundColor(Color.WHITE);nav.setPadding(dp(6),dp(6),dp(6),dp(6));
        String[] ns={"Calculator","Flat Pattern","Materials","FAQ"};for(int i=0;i<4;i++){final int q=i;Button b=new Button(this);b.setText(ns[i]);b.setTextSize(10);b.setAllCaps(false);b.setTextColor(i==active?BLUE:MUTED);b.setBackground(box(i==active?Color.rgb(239,246,255):Color.TRANSPARENT,Color.TRANSPARENT,10));nav.addView(b,new LinearLayout.LayoutParams(0,dp(50),1));b.setOnClickListener(v->{saveShared();if(q==0)showCalculator();else if(q==1)showFlat();else if(q==2)showMaterials();else showFaq();});}outer.addView(nav);setContentView(outer);
    }
    LinearLayout card(String title,String subtitle){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(16),dp(15),dp(16),dp(15));c.setBackground(box(CARD,BORDER,14));page.addView(c,new LinearLayout.LayoutParams(-1,-2));margins(c,0,0,0,12);TextView h=tv(title,18,TEXT);h.setTypeface(Typeface.DEFAULT,Typeface.BOLD);c.addView(h);if(subtitle!=null){TextView s=tv(subtitle,12,MUTED);c.addView(s);margins(s,0,2,0,8);}return c;}
    EditText field(LinearLayout c,String label,String value){TextView l=tv(label,13,TEXT);l.setTypeface(Typeface.DEFAULT,Typeface.BOLD);c.addView(l);margins(l,1,8,1,4);EditText e=new EditText(this);e.setText(value);e.setTextColor(TEXT);e.setTextSize(17);e.setSingleLine();e.setPadding(dp(12),0,dp(12),0);e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);e.setBackground(box(Color.WHITE,BORDER,9));c.addView(e,new LinearLayout.LayoutParams(-1,dp(50)));return e;}
    Spinner material(LinearLayout c){TextView l=tv("Material",13,TEXT);l.setTypeface(Typeface.DEFAULT,Typeface.BOLD);c.addView(l);margins(l,1,4,1,4);Spinner s=new Spinner(this);ArrayAdapter<String>a=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,names){@Override public View getView(int p,View v,ViewGroup g){TextView t=(TextView)super.getView(p,v,g);t.setTextColor(TEXT);t.setTextSize(16);t.setPadding(dp(12),0,dp(12),0);return t;}};s.setAdapter(a);s.setSelection(sharedMat);s.setBackground(box(Color.WHITE,BORDER,9));c.addView(s,new LinearLayout.LayoutParams(-1,dp(52)));return s;}
    Button action(String s){Button b=new Button(this);b.setText(s);b.setTextColor(Color.WHITE);b.setTextSize(15);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setBackground(box(BLUE,Color.TRANSPARENT,10));page.addView(b,new LinearLayout.LayoutParams(-1,dp(54)));margins(b,0,2,0,12);return b;}
    void units(){LinearLayout r=new LinearLayout(this);r.setBackground(box(Color.WHITE,BORDER,10));r.setPadding(dp(3),dp(3),dp(3),dp(3));page.addView(r,new LinearLayout.LayoutParams(-1,dp(48)));margins(r,0,0,0,12);unitIn=new Button(this);unitMm=new Button(this);unitIn.setText("INCH");unitMm.setText("MM");for(Button b:new Button[]{unitIn,unitMm}){b.setAllCaps(false);b.setTextColor(TEXT);r.addView(b,new LinearLayout.LayoutParams(0,-1,1));}unitIn.setOnClickListener(v->{metric=false;paintUnits();});unitMm.setOnClickListener(v->{metric=true;paintUnits();});paintUnits();}
    void paintUnits(){unitIn.setBackground(box(!metric?Color.rgb(219,234,254):Color.TRANSPARENT,Color.TRANSPARENT,8));unitMm.setBackground(box(metric?Color.rgb(219,234,254):Color.TRANSPARENT,Color.TRANSPARENT,8));}
    double val(EditText e){try{return Double.parseDouble(e.getText().toString());}catch(Exception x){return Double.NaN;}}
    String str(EditText e){return e==null?"":e.getText().toString();}
    void saveShared(){if(mat!=null)sharedMat=mat.getSelectedItemPosition();if(thick!=null)sharedT=str(thick);if(die!=null)sharedD=str(die);if(punch!=null)sharedR=str(punch);if(angle!=null&&!str(angle).isEmpty())sharedA=str(angle);if(length!=null)sharedL=str(length);}
    void result(String head,String body){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(16),dp(14),dp(16),dp(14));c.setBackground(box(Color.rgb(248,250,252),BORDER,12));results.addView(c,new LinearLayout.LayoutParams(-1,-2));margins(c,0,0,0,9);TextView h=tv(head,12,BLUE);h.setTypeface(Typeface.DEFAULT,Typeface.BOLD);c.addView(h);TextView b=tv(body,16,TEXT);b.setLineSpacing(0,1.2f);c.addView(b);margins(b,0,6,0,0);}

    void showCalculator(){
        shell("Bend Deduction Calculator","Accurate bend calculations for sheet metal fabrication",0);
        LinearLayout c=card("Bend Setup","Enter your material, tooling and bend dimensions.");mat=material(c);thick=field(c,"Material Thickness",sharedT);die=field(c,"V-Die Opening",sharedD);punch=field(c,"Punch Radius",sharedR);angle=field(c,"Bend Angle",sharedA);length=field(c,"Bend Length",sharedL);
        units();Button go=action("Calculate Bend");results=new LinearLayout(this);results.setOrientation(LinearLayout.VERTICAL);page.addView(results);go.setOnClickListener(v->calc());
    }
    void calc(){saveShared();results.removeAllViews();double t=val(thick),v=val(die),r=val(punch),a=val(angle),bl=val(length);if(metric){t/=25.4;v/=25.4;r/=25.4;bl/=25.4;}if(!(t>0&&v>0&&r>=0&&a>0&&a<180&&bl>0)){result("CHECK INPUTS","Complete all required bend dimensions.");return;}int i=sharedMat;double eff=Math.max(r,v/6),rad=Math.toRadians(a),ba=rad*(eff+k[i]*t),ossb=Math.tan(rad/2)*(eff+t),bd=2*ossb-ba;double tonsFt=1.33*tensile[i]*t*t/v/2000.0,total=tonsFt*(bl/12),rec=Math.ceil(total*1.15),flange=v/2+bd/2,cv=metric?25.4:1;String u=metric?" mm":" in";result("BEND RESULTS",String.format(Locale.US,"Bend allowance  %.3f%s\nBend deduction  %.3f%s\nEffective inside radius  %.3f%s",ba*cv,u,bd*cv,u,eff*cv,u));result("TONNAGE",String.format(Locale.US,"Required  %.2f tons\nRecommended  %.0f tons",total,rec));result("TOOLING",String.format(Locale.US,"Shortest flange  %.3f%s",flange*cv,u));}

    void showFlat(){
        shell("Flat Pattern Calculator","Use the same bend setup to calculate developed length",1);
        LinearLayout ref=card("Reference Data","Carried automatically from the Bend Deduction Calculator.");mat=material(ref);thick=field(ref,"Material Thickness",sharedT);die=field(ref,"V-Die Opening",sharedD);punch=field(ref,"Punch Radius",sharedR);angle=field(ref,"Bend Angle",sharedA);
        LinearLayout dims=card("Part Dimensions","Changes to reference data are carried back to the Bend Calculator.");f1=field(dims,"Flange 1","");f2=field(dims,"Flange 2","");bends=field(dims,"Number of Bends","1");Button go=action("Calculate Flat Pattern");results=new LinearLayout(this);results.setOrientation(LinearLayout.VERTICAL);page.addView(results);
        go.setOnClickListener(v->{saveShared();results.removeAllViews();double a1=val(f1),a2=val(f2),n=val(bends),t=val(thick),d=val(die),r=val(punch),ang=val(angle);if(!(a1>0&&a2>0&&n>=1&&t>0&&d>0&&r>=0&&ang>0)){result("CHECK INPUTS","Complete all required dimensions.");return;}int i=sharedMat;double er=Math.max(r,d/6),rr=Math.toRadians(ang),ba=rr*(er+k[i]*t),bd=2*Math.tan(rr/2)*(er+t)-ba,flat=a1+a2-n*bd;result("DEVELOPED FLAT",String.format(Locale.US,"Flange total  %.3f\nBend deduction  %.3f × %.0f\nFlat length  %.3f",a1+a2,bd,n,flat));});
    }
    void showMaterials(){shell("Material Library","Built-in BendMaster material reference",2);for(int i=0;i<names.length;i++){LinearLayout c=card(names[i],cats[i].toUpperCase(Locale.US));TextView x=tv(String.format(Locale.US,"Tensile Strength   %,.0f PSI\nK-Factor   %.2f",tensile[i],k[i]),15,TEXT);c.addView(x);}}
    void showFaq(){shell("Fabrication FAQ","Press brake terms and quick reference",3);String[][] q={{"K-Factor","Location of the neutral axis through material thickness."},{"Bend Allowance","Arc length of the neutral axis through the bend."},{"Bend Deduction","Amount subtracted from outside flange dimensions to obtain the developed flat."},{"V-Die Opening","Width of the lower die opening used for air bending."},{"Tonnage","Estimated forming force. Always verify press brake and tooling capacities."}};for(String[] x:q){LinearLayout c=card(x[0],null);c.addView(tv(x[1],14,MUTED));}}
}
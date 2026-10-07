package com.bendmasterpro.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
 final int BG=Color.rgb(15,27,47), CARD=Color.rgb(30,43,63), INPUT=Color.rgb(53,70,91), BORDER=Color.rgb(48,65,86), TXT=Color.rgb(239,243,248), MUT=Color.rgb(148,163,184), BLUE=Color.rgb(96,165,250);
 LinearLayout page,resultBox,customDieWrap; Spinner mat,dieSpin; EditText thick,punch,angle,length,f1,f2,bends,customDie; TextView punchConv,lenConv; boolean metric=false;
 int sharedMat=2; String sharedT="",sharedD="",sharedR="0.030",sharedA="90",sharedL="1";
 final String[] names={"5052-H32 Aluminum","6061-T6 Aluminum","A572 Gr42","A572 Gr50","CRS","HRPO A36","304 Stainless","316 Stainless"};
 final String[] cats={"aluminum","aluminum","steel","steel","steel","steel","stainless","stainless"};
 final double[] k={.41,.41,.44,.44,.44,.44,.43,.43}, tensile={33000,45000,60000,65000,55000,58000,75000,85000};
 String[] dies;
 int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
 GradientDrawable box(int c,int stroke,int rad){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp(rad));g.setStroke(dp(1),stroke);return g;}
 TextView t(String s,float z,int c){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(c);return v;}
 void mg(View v,int l,int top,int r,int b){ViewGroup.MarginLayoutParams p=(ViewGroup.MarginLayoutParams)v.getLayoutParams();p.setMargins(dp(l),dp(top),dp(r),dp(b));v.setLayoutParams(p);}
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.BLACK);getWindow().setNavigationBarColor(BG);dies=dieOptions();showCalc();}
 String[] dieOptions(){ArrayList<String> a=new ArrayList<>();a.add("Select die width");for(int mm=5;mm<=30;mm++){if(metric)a.add(mm+" mm ("+String.format(Locale.US,"%.3f",mm/25.4)+" in)");else a.add(String.format(Locale.US,"%.3f in (%d mm)",mm/25.4,mm));}a.add("Custom...");return a.toArray(new String[0]);}
 double selectedDie(){if(dieSpin==null)return Double.NaN;int p=dieSpin.getSelectedItemPosition();if(p<=0)return Double.NaN;if(p==dies.length-1){double x=val(customDie);return metric?x/25.4:x;}int mm=p+4;return mm/25.4;}
 void setupDie(LinearLayout parent){dies=dieOptions();dieSpin=spinner(parent,metric?"Die Width (mm)":"Die Width (in)",dies);customDieWrap=new LinearLayout(this);customDieWrap.setOrientation(LinearLayout.VERTICAL);parent.addView(customDieWrap);customDie=field(customDieWrap,metric?"Custom Die Width (mm)":"Custom Die Width (in)","");customDieWrap.setVisibility(View.GONE);dieSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int pos,long id){customDieWrap.setVisibility(pos==dies.length-1?View.VISIBLE:View.GONE);}});if(!sharedD.isEmpty()){try{double in=Double.parseDouble(sharedD),mm=in*25.4;int whole=(int)Math.round(mm);if(whole>=5&&whole<=30&&Math.abs(mm-whole)<.02)dieSpin.setSelection(whole-4);else{dieSpin.setSelection(dies.length-1);customDie.setText(metric?String.format(Locale.US,"%.3f",mm):sharedD);}}catch(Exception e){}}}
 void shell(int active){
  LinearLayout outer=new LinearLayout(this);outer.setOrientation(LinearLayout.VERTICAL);outer.setBackgroundColor(BG);
  ViewCompat.setOnApplyWindowInsetsListener(outer,(v,i)->{Insets x=i.getInsets(WindowInsetsCompat.Type.systemBars());v.setPadding(0,x.top,0,x.bottom);return i;});
  ScrollView sv=new ScrollView(this);page=new LinearLayout(this);page.setOrientation(LinearLayout.VERTICAL);page.setPadding(dp(17),dp(30),dp(17),dp(30));sv.addView(page);outer.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
  LinearLayout nav=new LinearLayout(this);nav.setBackgroundColor(Color.rgb(12,22,38));nav.setPadding(0,0,0,dp(4));String[] n={"Calculator","Flat","Materials","FAQ"};
  for(int i=0;i<4;i++){final int q=i;Button b=new Button(this);b.setText(n[i]);b.setTextSize(10);b.setAllCaps(false);b.setTextColor(i==active?BLUE:MUT);b.setBackgroundColor(Color.TRANSPARENT);nav.addView(b,new LinearLayout.LayoutParams(0,dp(52),1));b.setOnClickListener(v->{save();if(q==0)showCalc();else if(q==1)showFlat();else if(q==2)showMaterials();else showFaq();});}outer.addView(nav,new LinearLayout.LayoutParams(-1,dp(56)));setContentView(outer);ViewCompat.requestApplyInsets(outer);
 }
 LinearLayout card(String head){
  LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setBackground(box(CARD,BORDER,12));page.addView(c,new LinearLayout.LayoutParams(-1,-2));mg(c,0,0,0,27);
  TextView h=t(head,18,TXT);h.setTypeface(Typeface.DEFAULT,Typeface.BOLD);h.setPadding(dp(27),dp(15),dp(27),dp(15));c.addView(h);
  View line=new View(this);line.setBackgroundColor(BORDER);c.addView(line,new LinearLayout.LayoutParams(-1,dp(1)));
  LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(27),dp(14),dp(27),dp(20));c.addView(body);return body;
 }
 TextView label(LinearLayout c,String s){TextView l=t(s,15,TXT);l.setTypeface(Typeface.DEFAULT,Typeface.BOLD);c.addView(l);mg(l,0,10,0,9);return l;}
 EditText field(LinearLayout c,String lab,String val){label(c,lab);EditText e=new EditText(this);e.setText(val);if(lab.startsWith("Material Thickness"))e.setHint(metric?"3.175":"0.125");e.setTextColor(TXT);e.setHintTextColor(Color.rgb(113,128,146));e.setTextSize(17);e.setSingleLine();e.setPadding(dp(14),0,dp(14),0);e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);e.setBackground(box(INPUT,Color.rgb(67,86,108),7));c.addView(e,new LinearLayout.LayoutParams(-1,dp(42)));mg(e,0,0,0,10);return e;}
 Spinner spinner(LinearLayout c,String lab,String[] arr){label(c,lab);Spinner s=new Spinner(this);ArrayAdapter<String>a=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,arr){@Override public View getView(int p,View v,ViewGroup g){TextView z=(TextView)super.getView(p,v,g);z.setTextColor(TXT);z.setTextSize(16);z.setPadding(dp(14),0,dp(14),0);return z;}};s.setAdapter(a);s.setBackground(box(INPUT,Color.rgb(67,86,108),7));c.addView(s,new LinearLayout.LayoutParams(-1,dp(42)));mg(s,0,0,0,8);return s;}
 void helper(LinearLayout c,String s){TextView v=t(s,13,BLUE);c.addView(v);mg(v,0,0,0,16);}
 double val(EditText e){try{return Double.parseDouble(e.getText().toString());}catch(Exception x){return Double.NaN;}}
 void save(){if(mat!=null)sharedMat=mat.getSelectedItemPosition();if(thick!=null)sharedT=thick.getText().toString();if(dieSpin!=null&&dieSpin.getSelectedItemPosition()>0){double sd=selectedDie();if(sd>0)sharedD=String.format(Locale.US,"%.6f",sd);}if(punch!=null)sharedR=punch.getText().toString();if(angle!=null)sharedA=angle.getText().toString();if(length!=null)sharedL=length.getText().toString();}
 void watch(EditText e,TextView v){e.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){try{v.setText(String.format(Locale.US,"≈ %.3f mm",Double.parseDouble(s.toString())*25.4));}catch(Exception x){v.setText("");}}public void afterTextChanged(Editable e){}});}
 void showCalc(){
  shell(0);LinearLayout in=card("Input Parameters");mat=spinner(in,"Material Type",names);mat.setSelection(sharedMat);helper(in,"▱  Manage material library");
  thick=field(in,"Material Thickness (in)",sharedT);setupDie(in);
  LinearLayout pr=new LinearLayout(this);pr.setOrientation(LinearLayout.HORIZONTAL);TextView pl=label(pr,"Punch Radius (in)");punchConv=t("",13,BLUE);pr.addView(punchConv);in.addView(pr);punch=fieldNoLabel(in,sharedR);
  angle=field(in,"Bend Angle (degrees)",sharedA);LinearLayout lr=new LinearLayout(this);lr.setOrientation(LinearLayout.HORIZONTAL);TextView ll=label(lr,"Bend Length (in)");lenConv=t("",13,BLUE);lr.addView(lenConv);in.addView(lr);length=fieldNoLabel(in,sharedL);watch(punch,punchConv);watch(length,lenConv);punch.setText(sharedR);length.setText(sharedL);
  LinearLayout res=card("Calculation Result");resultBox=res;emptyResult();setupAutoCalc();
  LinearLayout q=card("Quick Reference");ref(q,"Die Size Rule","Steel/Stainless: 4-6× thickness | Aluminum: 6-8× thickness");ref(q,"Common K-Factors","Soft materials: 0.38-0.41 | Hard materials: 0.43-0.46");ref(q,"Typical Punch Radius","Usually 0.031\" - 0.125\" depending on material and tooling");
 }
 EditText fieldNoLabel(LinearLayout c,String val){EditText e=new EditText(this);e.setText(val);e.setTextColor(TXT);e.setTextSize(17);e.setSingleLine();e.setPadding(dp(14),0,dp(14),0);e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);e.setBackground(box(INPUT,Color.rgb(67,86,108),7));c.addView(e,new LinearLayout.LayoutParams(-1,dp(42)));mg(e,0,0,0,10);return e;}
 void ref(LinearLayout c,String h,String b){TextView x=t(h,13,TXT);x.setTypeface(Typeface.DEFAULT,Typeface.BOLD);c.addView(x);mg(x,0,8,0,7);TextView y=t(b,13,MUT);c.addView(y);mg(y,0,0,0,7);}
 void emptyResult(){resultBox.removeAllViews();Space s=new Space(this);resultBox.addView(s,new LinearLayout.LayoutParams(1,dp(45)));TextView icon=t("▣",36,MUT);icon.setGravity(Gravity.CENTER);resultBox.addView(icon);TextView h=t("Enter all parameters",17,TXT);h.setTypeface(Typeface.DEFAULT,Typeface.BOLD);h.setGravity(Gravity.CENTER);resultBox.addView(h);mg(h,0,18,0,5);TextView b=t("to calculate bend deduction",14,MUT);b.setGravity(Gravity.CENTER);resultBox.addView(b);Space z=new Space(this);resultBox.addView(z,new LinearLayout.LayoutParams(1,dp(45)));}
 void setupAutoCalc(){TextWatcher w=new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){}public void afterTextChanged(Editable e){autoCalc();}};for(EditText e:new EditText[]{thick,punch,angle,length,customDie})if(e!=null)e.addTextChangedListener(w);mat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int pos,long id){sharedMat=pos;autoCalc();}});AdapterView.OnItemSelectedListener old=null;dieSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int pos,long id){if(customDieWrap!=null)customDieWrap.setVisibility(pos==dies.length-1?View.VISIBLE:View.GONE);autoCalc();}});autoCalc();}
 void autoCalc(){double th=val(thick),r=val(punch),a=val(angle),bl=val(length),d=selectedDie();if(th>0&&r>=0&&a>0&&a<180&&bl>0&&d>0)calc();else if(resultBox!=null)emptyResult();}
 void calc(){save();resultBox.removeAllViews();double th=val(thick),r=val(punch),a=val(angle),bl=val(length);double d=selectedDie();if(!(th>0&&r>=0&&a>0&&a<180&&bl>0&&d>0)){TextView e=t("Enter all parameters to calculate bend deduction.",15,MUT);e.setGravity(Gravity.CENTER);resultBox.addView(e);return;}int i=sharedMat;double er=Math.max(r,d/6),rr=Math.toRadians(a),ba=rr*(er+k[i]*th),bd=2*Math.tan(rr/2)*(er+th)-ba,tonsFt=1.33*tensile[i]*th*th/d/2000,total=tonsFt*(bl/12),fl=d/2+bd/2;TextView o=t(String.format(Locale.US,"Bend Deduction\n%.4f in\n\nBend Allowance\n%.4f in\n\nEffective Inside Radius\n%.4f in\n\nRequired Tonnage\n%.2f tons\n\nRecommended Tonnage (+15%%)\n%.0f tons\n\nMinimum Flange\n%.4f in",bd,ba,er,total,Math.ceil(total*1.15),fl),16,TXT);o.setLineSpacing(0,1.15f);resultBox.addView(o);}
 void showFlat(){shell(1);LinearLayout d=card("Flat Pattern");f1=field(d,"Flange 1 (in)","");f2=field(d,"Flange 2 (in)","");bends=field(d,"Number of Bends","1");LinearLayout r=card("Reference Data");mat=spinner(r,"Material Type",names);mat.setSelection(sharedMat);thick=field(r,"Material Thickness (in)",sharedT);setupDie(r);punch=field(r,"Punch Radius (in)",sharedR);angle=field(r,"Bend Angle (degrees)",sharedA);TextView note=t("Reference data is shared with the Bend Deduction Calculator.",13,BLUE);r.addView(note);}
 void showMaterials(){shell(2);for(int i=0;i<names.length;i++){LinearLayout c=card(names[i]);ref(c,"Tensile Strength",String.format(Locale.US,"%,.0f PSI",tensile[i]));ref(c,"K-Factor",String.format(Locale.US,"%.2f",k[i]));}}
 void showFaq(){shell(3);String[][] x={{"K-Factor","Neutral-axis location through material thickness."},{"Bend Allowance","Arc length of the neutral axis through the bend."},{"Bend Deduction","Amount subtracted from outside flange dimensions to obtain developed flat length."},{"V-Die Opening","Width of the lower die opening used for air bending."},{"Tonnage","Estimated forming force; verify machine and tooling capacity."}};for(String[] q:x){LinearLayout c=card(q[0]);TextView b=t(q[1],14,MUT);c.addView(b);}}
}
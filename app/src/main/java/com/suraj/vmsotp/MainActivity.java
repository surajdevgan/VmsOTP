package com.suraj.vmsotp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView img, imglogo;
    TextView txtresult, nameser, wnames, Or;
    Button scan, ServiceScan, ChooseCode;
    Uri uri, imageUri;
    RequestQueue requestQueue;
    public Bitmap mbitmap;
    public static final int PICK_IMAGE = 1;
    CardView cardViewButton = null;
    CardView cardViewButton1 = null;
    CardView gcardViewButton = null;
    CardView gcardViewButton1 = null;
    EditText editText = null;
    String sex = " ";
    EditText geditText = null;
    String newString = "";
    String value1 = "", value0 = "";
    String LMG = "", MG = "", RMG = "";
    String PLMG = "", PMG = "", PRMG = "";
    int c = 0;
    String name = "";
    String version = "";
    String OTPCODE = "";
    int printCount = 0;
    String gname = "";
    String gversion = "";
    String gOTPCODE = "";
    TextView textViewVersion, RemainingCount;
    EditText textViewName, ServiceEdit, gtextViewName, PrintID,gtextViewVersion;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    int count = 0, id = 0;
    String userName = "";
    String wname;
    Dialog dialog, pdialog;
    ProgressDialog pd;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RemainingCount = findViewById(R.id.remainpc);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        preferences = getSharedPreferences(Util.AcuPrefs, MODE_PRIVATE);
        editor = preferences.edit();
        count = Integer.parseInt(preferences.getString(Util.count, ""));
        wname = preferences.getString(Util.Name, "");

        id = preferences.getInt(Util.id, 0);
        userName = preferences.getString(Util.Name, "");
        requestQueue = Volley.newRequestQueue(this);
        scan = (findViewById(R.id.scan));
        img = (findViewById(R.id.imgview));
        ServiceEdit = findViewById(R.id.editser);
        Or = findViewById(R.id.tvs);
        wnames = findViewById(R.id.wname);
        nameser = findViewById(R.id.namenn);
        imglogo = findViewById(R.id.logoimg);
        ServiceScan = findViewById(R.id.scanservice);
        ChooseCode = findViewById(R.id.slectimg);
        txtresult = (findViewById(R.id.txtResult));
        scan.setVisibility(View.GONE);
        pd = new ProgressDialog(this);
        pd.setMessage("Generating OTP..");
        pd.setCancelable(false);
        ServiceEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ser = ServiceEdit.getText().toString();
                int ll = ser.length();
                if (ll > 11) {
                    ServiceScan.setVisibility(View.VISIBLE);
                    Or.setVisibility(View.GONE);
                    ChooseCode.setVisibility(View.GONE);


                }
                if (ll < 12) {
                    ServiceScan.setVisibility(View.GONE);
                    Or.setVisibility(View.VISIBLE);
                    ChooseCode.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void btnScan(View view) {


        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        Frame frame = new Frame.Builder()
                .setBitmap(mbitmap)
                .build();

        SparseArray<Barcode> barcodeSparseArray = detector.detect(frame);

        if (barcodeSparseArray.size() > 0) {
            Barcode result = barcodeSparseArray.valueAt(0);
            String serviceOTP = scanTxt(result.rawValue);
            if (serviceOTP.contains("#") || serviceOTP.contains("@") || serviceOTP.contains("%") || serviceOTP.contains("&")) {
                String[] otp = serviceOTP.split(";");
                name = otp[1];
                version = otp[2];
                OTPCODE = otp[0];
                showDialouge();
            } else {
                shareWhatsapp(serviceOTP);
            }
        } else {
            Toast.makeText(this, "Select a QR code Or QR Code is Not Clear", Toast.LENGTH_SHORT).show();
        }
    }

    public void SelectImg(View view) {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Qr Code"), PICK_IMAGE);
        txtresult.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            uri = data.getData();
            try {
                mbitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                img.setImageBitmap(mbitmap);
                imglogo.setVisibility(View.GONE);
                nameser.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                wnames.setVisibility(View.GONE);
                ServiceEdit.setVisibility(View.GONE);
                Or.setVisibility(View.GONE);


                scan.setVisibility(View.VISIBLE);
            } catch (IOException e) {

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 101, 0, "Logout");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 101:
                editor.clear();
                editor.apply();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
        return true;
    }


    //  eh shayad serive otp da logic va

    String scanTxt(String raw) {
        StringBuilder Id = new StringBuilder();
        if (raw.contains("#") || raw.contains("@") || raw.contains("%") || raw.contains("&")) {
            return raw;
        } else {

            Log.w("newoldraw",raw);

            Log.w("lastval",raw);
            String LastFour = raw.substring(8,12);
            Log.w("lastfour",LastFour);

            char character = raw.charAt(11);
            int ASCII = (int) character;


            if(ASCII==49)
            {
                ASCII = 57;
            }

            else if(ASCII==65)
            {
                ASCII = 90;

            }

            else {
                ASCII = ASCII - 1;


            }

            char charAscii = (char) ASCII;


            raw = raw.replace(character,charAscii);
            Log.w("newraw",raw);




            for (int i = 0; i <= 11; i++) {
                if (i == 4 || i == 8) {
                    Id.append(" ");
                }
                char d = raw.charAt(i);

                if (Character.isLetter(d)) {
                     ASCII = d;
                    ASCII = ASCII + i;
                    if (ASCII == 91) {
                        d = 'o';
                    } else if (ASCII == 92) {
                        d = 'p';
                    } else if (ASCII == 93) {
                        d = 'q';
                    } else if (ASCII == 94) {
                        d = 'r';
                    } else if (ASCII == 95) {
                        d = 's';
                    } else if (ASCII == 96) {
                        d = 't';
                    } else {
                        d = (char) ASCII;
                    }
                    Id.append(d);
                } else {
                    Id.append(d);
                }
            }


            Toast.makeText(this, Id, Toast.LENGTH_SHORT).show();
            Log.w("newid",String.valueOf(Id));
            Log.w("newlastraw",raw);
            return String.valueOf(Id);
        }
    }


    void shareWhatsapp(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Service OTP");
        intent.putExtra(Intent.EXTRA_TEXT, "This is Service OTP " + text);
        startActivity(Intent.createChooser(intent, "This is Service OTP " + text));
    }

    void shareWhatsappprint(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Service OTP");
        intent.putExtra(Intent.EXTRA_TEXT, "This is Print Count OTP " + text);
        startActivity(Intent.createChooser(intent, "This is Print Count OTP " + text));
    }

    void showDialouge() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.print_count_dialouge);
        dialog.setTitle("Print Count OTP");
        dialog.setCancelable(false);
        cardViewButton = dialog.findViewById(R.id.generate);
        cardViewButton1 = dialog.findViewById(R.id.cancel);
        editText = dialog.findViewById(R.id.editText3);
        textViewName = dialog.findViewById(R.id.textName);
        textViewVersion = dialog.findViewById(R.id.textVersion);
        textViewName.setText(name);
        textViewVersion.setText(version);

        cardViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String a = editText.getText().toString();
                if (a.isEmpty()) {
                    editText.setError("Enter the Value of Print Count");
                    editText.requestFocus();
                    return;
                }

                if(textViewName.getText().toString().isEmpty())
                {
                    textViewName.setError("Enter the name");
                    textViewName.requestFocus();
                    return;
                }

                if (a.startsWith(String.valueOf(0))) {
                    editText.setError("Enter Correct Value");
                    editText.requestFocus();
                    return;

                }


                if (ConnectionCheck.isConnected(connectivityManager, networkInfo, MainActivity.this)) {
                    if (isVarified()) {
                        printCount = Integer.parseInt(editText.getText().toString());
                        if (count > printCount || count == printCount) {
                            count = count - printCount;
                            sex = textViewName.getText().toString();
                            uploadDetails();
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to Generate Print Count OTP", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Internet Connection Required", Toast.LENGTH_LONG).show();
                }
            }
        });
        cardViewButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    boolean isVarified() {
        boolean isValidate = true;
        String i = editText.getText().toString();


        try {

            int printint = Integer.parseInt(i);
            if (printint == 00) {
                editText.setError("Enter correct value");
                editText.requestFocus();
                isValidate = false;
            }


        } catch (Exception e) {

        }


        if (Integer.parseInt(i) % 100 != 0) {
            isValidate = false;
            editText.setError("Not Multiple of 100");
        }
        if (Integer.parseInt(i) > 9999) {
            isValidate = false;
            editText.setError("Greater Than 9999");
        }


        return isValidate;
    }

    boolean isgVarified() {
        boolean isgValidate = true;
        String i = geditText.getText().toString();


        try {

            int printint = Integer.parseInt(i);
            if (printint == 00) {
                geditText.setError("Enter correct value");
                geditText.requestFocus();
                isgValidate = false;
            }


        } catch (Exception e) {

        }


        if (Integer.parseInt(i) % 100 != 0) {
            isgValidate = false;
            geditText.setError("Not Multiple of 100");
        }
        if (Integer.parseInt(i) > 9999) {
            isgValidate = false;
            geditText.setError("Greater Than 9999");
        }


        return isgValidate;
    }

    // compiled otp print count otp da logic va

    public String compileOTP(String raw) {
        raw = raw + " ";
        String[] val = new String[raw.length()];
        for (int i = 0; i <= raw.length() - 1; i++) {
            if (i % 4 == 0) {
                val[i] = raw.substring(c, i);
                c = i;
            }
        }

        LMG = val[4];
        MG = val[8];
        RMG = val[12];

//		                        Left Most Group Processing

        String LMD1 = LMG.substring(0, 1);
        String RD1 = LMG.substring(1, 4);
        String PDG1 = firstAlgo(RD1);

//                              Middle Group Processing

        String LMD2 = MG.substring(0, 1);
        String SLMD2 = MG.substring(1, 2);
        String RD2 = MG.substring(2, 4);
        String PDG2 = firstAlgo(RD2);

//                              Right Most Group Processing

        String LMD3 = RMG.substring(0, 1);
        String RD3 = RMG.substring(1, 4);
        String PDG3 = firstAlgo(RD3);

        String TPS = PDG1 + PDG2 + PDG3;

        for (int i = 0; i <= TPS.length() - 1; i++) {
            char d = TPS.charAt(i);
            if (Character.isLetter(d)) {
                int ASCII = d;
                ASCII = ASCII + i;
                if (ASCII == 91) {
                    d = 'o';
                } else if (ASCII == 92) {
                    d = 'p';
                } else if (ASCII == 93) {
                    d = 'q';
                } else if (ASCII == 94) {
                    d = 'r';
                } else if (ASCII == 95) {
                    d = 's';
                } else if (ASCII == 96) {
                    d = 't';
                } else {
                    d = (char) ASCII;
                }
                newString = newString + d;
            } else {
                newString = newString + d;
            }
        }

        PLMG = newString.substring(0, 3);
        PMG = newString.substring(3, 5);
        PRMG = newString.substring(5, 8);

        //                             Print Count Otp

        int divide = printCount / 100;
        if (String.valueOf(divide).length() == 1) {
            value1 = "0";
            value0 = String.valueOf(divide).substring(0, 1);
        } else {
            value1 = String.valueOf(divide).substring(0, 1);
            value0 = String.valueOf(divide).substring(1, 2);
        }

        char a = LMD1.charAt(0);

        char b = LMD2.charAt(0);
        char e = SLMD2.charAt(0);

        char d = LMD3.charAt(0);

        a = (char) ((int) a + Integer.parseInt(value1));            //LMD1

        d = (char) ((int) d + Integer.parseInt(value0));  //LMD3

        b = (char) ((int) b + Integer.parseInt(value1));      //LMD2

        e = (char) ((int) e + Integer.parseInt(value0));    //SLMD2

        String NLMG = finalAlgo(a) + PLMG;
        String NRMG = finalAlgo(d) + PRMG;
        String NMG = finalAlgo(b) + "" + finalAlgo(e) + "" + PMG;

        return NLMG + " " + NMG + " " + NRMG;
    }

    public String firstAlgo(String val) {
        StringBuilder PD = new StringBuilder();
        for (int i = 0; i <= val.length() - 1; i++) {
            char d = val.charAt(i);
            if (Character.isLetter(d)) {
                if (d == 'A') {
                    d = '@';
                } else if (d == 'B') {
                    d = '!';
                } else if (d == 'J') {
                    d = '%';
                } else if (d == 'W') {
                    d = '&';
                } else if (d == 'X') {
                    d = '*';
                }
            }
            PD.append(d);
        }
        return String.valueOf(PD);
    }

    public char finalAlgo(char val1) {
        if ((int) val1 == 39) {
            val1 = 'a';
        } else if ((int) val1 == 44) {
            val1 = 'b';
        } else if ((int) val1 == 45) {
            val1 = 'c';
        } else if ((int) val1 == 46) {
            val1 = 'd';
        } else if ((int) val1 == 58) {
            val1 = 'e';
        } else if ((int) val1 == 59) {
            val1 = 'f';
        }
        return val1;
    }

    void uploadDetails() {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, Util.updateAndInsert, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String message = object.getString("message");
                    if (message.contains("Sucessfully")) {
                        pd.dismiss();
                        String hi = compileOTP(OTPCODE);
                        shareWhatsappprint(hi);
                        if(pdialog !=null)
                        {
                            pdialog.dismiss();


                        }
                        if(dialog!=null)
                        {
                            dialog.dismiss();

                        }



                        finish();
                    } else {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    pd.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if(sex != " ")
                {


                    map.put("Client", sex);
                }
                if(sex  == " ")
                {
                    map.put("Client", name);


                }

                map.put("Version", version);
                map.put("Doneby", userName);

                map.put("Printcnt", String.valueOf(printCount));
                map.put("User_ID", String.valueOf(id));
                map.put("Count", String.valueOf(count));
                return map;
            }
        };
        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RemainingCount.setText(String.valueOf("Remaining Print Counts " + count));
        nameser.setText("Welcome ");
        wnames.setText(wname);
    }

    public void generate(View view) {
        String serviceOTP = scanTxt(ServiceEdit.getText().toString());
        ServiceEdit.setText("");
        shareWhatsapp(serviceOTP);
    }

    public void GeneratePrint(View view) {


        ShowDialog();


    }

    void ShowDialog() {


        pdialog = new Dialog(this);
        pdialog.setContentView(R.layout.generate_print_dialog);
        pdialog.setTitle("Print Count OTP");
        pdialog.setCancelable(false);
        PrintID = pdialog.findViewById(R.id.printid);
        gcardViewButton = pdialog.findViewById(R.id.ggenerate);
        gcardViewButton1 = pdialog.findViewById(R.id.gcancel);
        geditText = pdialog.findViewById(R.id.geditText3);
        gtextViewName = pdialog.findViewById(R.id.gtextName);
        gtextViewVersion = pdialog.findViewById(R.id.gtextVersion);
        spinner = pdialog.findViewById(R.id.typespinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        gcardViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = geditText.getText().toString();
                String b = PrintID.getText().toString();

                if(gtextViewName.getText().toString().isEmpty())
                {
                    gtextViewName.setError("Enter the name");
                    gtextViewName.requestFocus();
                    return;
                }
                if(b.length()<12)
                {
                    PrintID.setError("Incorrect value");
                    PrintID.requestFocus();
                    return;

                }
                if (b.isEmpty()) {
                    PrintID.setError("Print Id is require");
                    PrintID.requestFocus();
                    return;
                }
                if (a.isEmpty()) {
                    geditText.setError("Enter the Value of Print Count");
                    geditText.requestFocus();
                    return;
                }

                if (a.startsWith(String.valueOf(0))) {
                    geditText.setError("Enter Correct Value");
                    geditText.requestFocus();
                    return;

                }



                if (spinner.getSelectedItem().toString().trim().equals("Select sheet type....")) {
                    View sv = spinner.getSelectedView();
                    TextView tv = (TextView)sv;
                    tv.setError("Please select sheet");
                    tv.setTextColor(Color.RED);
                    spinner.requestFocus();
                    return;
                }

                    if (ConnectionCheck.isConnected(connectivityManager, networkInfo, MainActivity.this)) {
                    if (isgVarified()) {
                        name = gtextViewName.getText().toString();
                        OTPCODE = PrintID.getText().toString();
                        version = gtextViewVersion.getText().toString();
                        printCount = Integer.parseInt(geditText.getText().toString());
                        if (count > printCount || count == printCount) {
                            count = count - printCount;
                            uploadDetails();
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to Generate Print Count OTP", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Internet Connection Required", Toast.LENGTH_LONG).show();
                    }
                }





            }
        });
        gcardViewButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdialog.dismiss();

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });







        pdialog.show();

    }
}
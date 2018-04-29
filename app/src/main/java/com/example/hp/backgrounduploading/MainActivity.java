package com.example.hp.backgrounduploading;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button btn ;
    ImageView imageView ;

    int globalcount;

    //variables declared for the navigation view
    private DrawerLayout drawerLayout;
    Toolbar toolbar;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;



    private NavigationView navigationView;
    // private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle ;
    // Toolbar toolbar;

    /*  Display dp = new Display();
      String gallery = dp.gallery_name;*///need to send this variable to server
    String gallery = "pathadi"; //lol its like double declaration for the string

    //   public static String URL = "https://www.scholarshield.in/Classes/m1/images/uploadimage.php";
    public static String URL = "https://www.vidyaroha.com/uploadimage.php";
    //    public static String URL = "34.208.61.70/images/uploadimage.php";
    private static final String IMAGE_CAPTURE_FOLDER = "Classes/m1/Deepak/images";
    private static final int CAMERA_PIC_REQUEST = 1111; // some kind of id for camera request
    private Button btnCamera;
    private static File file;
    private static Uri _imagefileUri;
    private TextView resultText;
    private static String _bytes64Sting, _imageFileName;


    //variables for location display
    private Button button_lcn;
    private TextView textView_lcn;
    private LocationManager locationManager;
    private LocationListener listener;
    public Button bt2;

    private NotificationManager mnotifyManager;//small n and b to differentiate
    private NotificationCompat.Builder mbuilder;
    int not_id = 1;

    public Button view;
    public Button count;
    public Button delete;
    public Button view_with_id;
    EditText id ;
    public  Button getcounter;

    int count1 = 0;
    //int count2 = 0;
    //int flag = 0;

    //This imagepath should be stored one by one in this replacable variable stored_iamge_path and then shd go in the arraylist
    String stored_image_uri_path;


    ArrayList<String> list_Unsent_Images=new ArrayList<String>();//Creating arraylist.  can use it in combination with
    // list.add("Ravi");//Adding object in arraylist.
    //list.add("Vijay");

    String PREF_NAME = "list_unsent_images7";



    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = (EditText)findViewById(R.id.id);
        view = (Button)findViewById(R.id.view);
        count = (Button)findViewById(R.id.count);
        delete = (Button) findViewById(R.id.delete);
        view_with_id = (Button)findViewById(R.id.viewwithid);
        getcounter = (Button)findViewById(R.id.button3);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor res = myDb.getalldata();
                if(res.getCount() == 0){
                    //show message
                    showmessage("Error","No Data Found");
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()){
                    buffer.append("Id: "+ res.getString(0)+ "\n");
                    buffer.append("Image Path: "+ res.getString(1)+ "\n");

                }

                showmessage("Data",buffer.toString());

            }
        });
        getcounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = myDb.getalldata();
//                Toast.makeText(MainActivity.this, res.getCount(), Toast.LENGTH_SHORT).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deletedrows = myDb.deletedata(id.getText().toString());

                if(deletedrows > 0){
                    Toast.makeText(MainActivity.this, "Data Deleted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Data Not Deleted", Toast.LENGTH_SHORT).show();
                }

            }
        });
        view_with_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor res = myDb.getonedata(id.getText().toString());
                if(res.getCount() == 0){
                    //show message
                    showmessage("Error","No Data Found");
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()){
                    buffer.append("Id: "+ res.getString(0)+ "\n");
                    buffer.append("Image Path: "+ res.getString(1)+ "\n");

                }

                showmessage("Data",buffer.toString());

            }
        });



        isOnline();

        myDb = new DatabaseHelper(this);
        btn =(Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first thing we want is time when we want notification to show
                //in order to pick the time we can use the instance of the class calendar
                Calendar calendar = Calendar.getInstance();
                //repeating notification works by creating and registering a broadcast receiver
                //this will be triggered by the alarm manager(its a service in the android device)

                calendar.set(Calendar.HOUR_OF_DAY,7);
                calendar.set(Calendar.MINUTE,3);
                calendar.set(Calendar.SECOND,15);

                //let us create  a new intent which would lead us to the broadcast receiver
                Intent i = new Intent(getApplicationContext(),Notification_Receiver.class);
                // the alarm service takes a pending intent as a parameter
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),100,i,PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
               // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_FIFTEEN_MINUTES,pendingIntent);//RTC wakeup ensures that the alarm will be triggered even if teh device goes into the sleep mode
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 10000,30 * 1000,pendingIntent);//RTC wakeup ensures that the alarm will be triggered even if teh device goes into the sleep mode
                //Did not use calendar's time but used time of the system

            }
        });
        btnCamera = (Button) findViewById(R.id.button2);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();

            }
        });









        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void return_stored_image_uri(String str) {
        this.stored_image_uri_path = str;
    }

    private void captureImage() {
        //firstly we create an intent for the clicking of photo through camera
        _imageFileName = String.valueOf(System.currentTimeMillis());
        Intent intent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        _imagefileUri = Uri.fromFile(getFile());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imagefileUri);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_PIC_REQUEST) {
                try {
                    //  resultText = (TextView) findViewById(R.id.textView);
                    // resultText.setText("New file " + _imageFileName + ".jpg created\n");
                    //I need to store the imagepath only when the uploading fails.
                    return_stored_image_uri(_imagefileUri.getPath());
                    uploadImage(_imagefileUri.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //I need to store the imagepath only when the uploading fails.
              //  return_stored_image_uri(_imagefileUri.getPath());
            }
        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
            Toast.makeText(getApplicationContext(),
                    "User cancelled image capture", Toast.LENGTH_SHORT).show();

        } else {
            // failed to capture image
            Toast.makeText(getApplicationContext(),
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private void uploadImage(String picturePath) throws IOException {
        Bitmap bm = BitmapFactory.decodeFile(picturePath);

        //bug fixing regarding the image orientation

        ExifInterface ei = new ExifInterface(picturePath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        // I got the orientation of the image here

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        android.graphics.Matrix m = new android.graphics.Matrix();
        m.postRotate(rotationAngle);
        Bitmap rotatedImg = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);


        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        rotatedImg.compress(Bitmap.CompressFormat.JPEG, 50, bao);
        byte[] byteArray = bao.toByteArray();
        _bytes64Sting = Base64.encodeBytes(byteArray);
        RequestPackage rp = new RequestPackage();
        rp.setMethod("POST");
        rp.setUri(URL);
        rp.setSingleParam("base64", _bytes64Sting);
        rp.setSingleParam("ImageName", _imageFileName + ".jpg");
        rp.setSingleParam("Gallery", gallery);
      // Upload image to server
        new uploadToServer().execute(rp);

    }

    public class uploadToServer extends AsyncTask<RequestPackage, Void, String> {

        // private ProgressDialog pd = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            //the below thing may not be required
            resultText = (TextView) findViewById(R.id.textView);
            resultText.setText("New file " + _imageFileName + ".jpg created\n");
            // Displays the progress bar for the first time.
            mnotifyManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mbuilder = new NotificationCompat.Builder(getApplicationContext());
            mbuilder.setContentTitle("Picture Upload")
                    .setContentText("Upload in progress")
                    .setSmallIcon(R.drawable.upload);


        }


        @Override
        protected String doInBackground(RequestPackage... params) {
            mbuilder.setProgress(0, 0, true);
            // Displays the progress bar for the first time.
            mnotifyManager.notify(not_id, mbuilder.build());



            String content = MyHttpURLConnection.getData(params[0]);
            return content;

        }

        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            mbuilder.setContentText("Upload complete")
                    // Removes the progress bar
                    .setProgress(0,0,false);
            mnotifyManager.notify(not_id, mbuilder.build());





            //one delay option

            //commenting it right now
            int secs = 5; // Delay in seconds

            Utils.delay(secs, new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    // Do something after delay



                    resultText.setText(result);

                    //here  i shd delete the image path if uploaded after notification click
                    //    list_Unsent_Images.remove(list_Unsent_Images.get());


                    count1++; //becoz starting from zero
                    if(result != null && !result.trim().isEmpty()){
                        //flag = 0; //flag 0 means successfull uploading
                        Toast.makeText(getApplicationContext(),
                                "Finished Uploading Image number : " +  count1, Toast.LENGTH_SHORT)
                                .show();
                    }
                    else{
                        // flag = 1;//flag = 1 means that image has failed uploading
                        //to push the imagepath to the arraylist
                   //     Log.d("failed image path : ",stored_image_uri_path); cant print out coz screen not available

                        //storing to the database here!
                        list_Unsent_Images.add(stored_image_uri_path);
                        boolean isinserted = myDb.insertdata(stored_image_uri_path);
                        if(isinserted == true){
                            Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Data Not Inserted", Toast.LENGTH_SHORT).show();
                        }
                        int cnt = getintSharedPreferences(PREF_NAME,"count");
                        int cnt2 = cnt + 1 ;
                        setintPreferences(PREF_NAME,"count",cnt2);
                        setPreferences(PREF_NAME,"path"+String.valueOf(cnt2),stored_image_uri_path); //need to get the current picture path
                        // writeToFile(stored_image_uri_path,getApplicationContext());
                        // resend the image and again clear the flag to 0.

                        //Building up the notification for it.
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.nf)
                                        .setContentTitle("Some Images Failed Sending")
                                        .setContentText("CLICK TO RESEND");

                        Intent i = new Intent("do_something");
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);


                        mBuilder.setContentIntent(pendingIntent);
                        mBuilder.setAutoCancel(true);//to clear the  notification after clicking

                        // Sets an ID for the notification
                        int mNotificationId = 001;
                        // Gets an instance of the NotificationManager service
                        NotificationManager mNotifyMgr =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        // Builds the notification and issues it.
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());





                        // stored_image_uri_path = list_Unsent_Images.get(0); not a good idea

                        //printing the contents of my ArrayList here:
                        for(int p =0;p<list_Unsent_Images.size();p++){

                            String temp = list_Unsent_Images.get(p);
//                            Log.d("path bfr notf click: ", temp);
                            //Log.i(temp); //expecting int instead of String

                        }





                        //now to call the method on notification click(this is the prev method that din't work)


               /* final BroadcastReceiver call_method = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action_name = intent.getAction();
                        if (action_name.equals("call_method")) {

                            call_method();
                            // call your method here and do what ever you want.

                           /* Toast.makeText(getApplicationContext(),
                                    "Test methodcall  " , Toast.LENGTH_SHORT)
                                    .show();*/





/*
                        }
                    };
                };
                registerReceiver(call_method, new IntentFilter("call_method"));*/


                    }
         /*   final BroadcastReceiver call_method = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action_name = intent.getAction();
                    if (action_name.equals("call_method")) {

                        call_method();

                    }
                };
            };
            registerReceiver(call_method, new IntentFilter("call_method"));*/
                }
            });

        }
    }


    private File getFile() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(filepath, IMAGE_CAPTURE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        return new File(file + File.separator + _imageFileName
                + ".jpg");
    }



    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){

            //We are giving the toast here
            // Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("NO INTERNET CONNECTION!")
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return true;
    }


    public void setPreferences(String preferenceName, String key, String value) {
        SharedPreferences settings = getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setintPreferences(String preferenceName, String key, Integer value) {
        SharedPreferences settings = getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public Integer getintSharedPreferences(String preferenceName, String key) {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(preferenceName, 0);
        Integer value = settings.getInt(key, 0);
        return value;
    }

    public String getSharedPreferences(String preferenceName, String key) {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(preferenceName, 0);
        String value = settings.getString(key, "");
        return value;
    }

    public  void deletesharedspecificpreference(String preferenceName ,String key){
        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.remove(key);
        editor.apply();
    }

    public void showmessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

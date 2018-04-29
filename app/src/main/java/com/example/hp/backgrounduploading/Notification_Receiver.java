package com.example.hp.backgrounduploading;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Deepak on 03-06-2017.
 */
public class Notification_Receiver extends BroadcastReceiver{

    public static String URL = "https://www.vidyaroha.com/uploadimage.php";
    private static File file;
    private static Uri _imagefileUri;

    int counter;
    String stored_image_uri_path;
    int not_id2 = 175;

    private static String _bytes64Sting, _imageFileName;
    String gallery = "pathadi";
    DatabaseHelper myDb;

    private NotificationManager mnotifyManager;//small n and b to differentiate
    private NotificationCompat.Builder mbuilder;
    int not_id = 1;

    Context globalcontext;

    String global_unsent_path ;

    String global_picture_path;

    ArrayList<String> list_Unsent_Ids=new ArrayList<String>();
    ArrayList<String> list_Unsent_Images_Paths=new ArrayList<String>();

    int flag;

    @Override
    public void onReceive(Context context, Intent intent) {

        myDb = new DatabaseHelper(context);
        globalcontext = context;

        Cursor res = myDb.getalldata();
        int count = res.getCount();//just for getting the count of number of elements present

        //Toast.makeText(MainActivity.this, res.getCount(), Toast.LENGTH_SHORT).show();

        //overiding the alarm manager to start background uploading

        //this is the task that we are overriding over the alarm manager services

        //here goes the code to build our notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context,Repeating_activity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.not)
                .setContentTitle("PHOTOS BEING UPLOADED")
                .setContentText("UNSENT IMAGES COUNT : "+ count)
                .setAutoCancel(true);    //makes notification dismissable when the user swipes it right

      notificationManager.notify(not_id2,builder.build());


        //now i have to upload the image based on path from the database
        //this will be done in a loop


        Cursor res2= myDb.getalldata();
        if(res2.getCount() == 0){
            //do nothing
        }
        else {

            while (res2.moveToNext()) {
                list_Unsent_Ids.add(res2.getString(0));
                list_Unsent_Images_Paths.add(res2.getString(1));
            }
        }

        for(int i = 0;i < list_Unsent_Ids.size();i++) //No need error is not here
            {
            Log.d("Unsent id "+ i,list_Unsent_Ids.get(i));
            Log.d("Unsent path "+ i,list_Unsent_Images_Paths.get(i));

        }

        //the global variable flag to be used here
        flag = 0 ;

        while (list_Unsent_Images_Paths.size()>0 && flag == 0 ) {

           // for (int counter = 0; counter < list_Unsent_Images_Paths.size(); counter++) {

//                stored_image_uri_path = list_Unsent_Images_Paths.get(counter);
            stored_image_uri_path = list_Unsent_Images_Paths.get(0);

            global_unsent_path = list_Unsent_Images_Paths.get(0); //the global unsent path


            Log.d("deepak strd_uri_path : ",list_Unsent_Images_Paths.get(0));
           // update_stored_image_uri_path(list_Unsent_Images_Paths.get(0)); //updated the path

                try {
//                    Toast.makeText(globalcontext,list_Unsent_Images_Paths.get(counter) , Toast.LENGTH_SHORT).show();
//                    Toast.makeText(globalcontext,list_Unsent_Ids.get(counter) , Toast.LENGTH_SHORT).show();
                   // uploadImage(list_Unsent_Images_Paths.get(counter));
                    uploadImage(list_Unsent_Images_Paths.get(0));

//                    uploadImage(list_Unsent_Images_Paths.get(counter));//called upload image here with path of the img strd in list
                    //Log.d("uploain img", list_Unsent_Images.get(counter));

                    /*shd i remove or not bcz cant figure out the index*/
                    if(list_Unsent_Ids.size() > 0) {
                        myDb.deletedata(list_Unsent_Ids.get(0)); // the error is of index not matching //the counter and '0' don't match
                    }

                    Log.d("deleted id : ",list_Unsent_Ids.get(0));
                    Log.d("deleted path : ",list_Unsent_Images_Paths.get(0));

                    list_Unsent_Images_Paths.remove(list_Unsent_Images_Paths.get(0));
                    list_Unsent_Ids.remove(list_Unsent_Ids.get(0));



//                    list_Unsent_Images_Paths.remove(list_Unsent_Images_Paths.get(0));
//                    list_Unsent_Ids.remove(list_Unsent_Ids.get(0));
//                    list_Unsent_Ids.remove(list_Unsent_Images_Paths.get(counter));


//                    myDb.deletedata(list_Unsent_Ids.get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }

          //  }
        }

    }

   public void update_stored_image_uri_path(String new_uri_path ){

        stored_image_uri_path = new_uri_path;
    }

    public String get_updated_stored_image_uri_path( ){

        return  stored_image_uri_path ;
    }


    private void uploadImage(String picturePath) throws IOException {
        Bitmap bm = BitmapFactory.decodeFile(picturePath);

        //changing the flag variable
        flag = 1 ;

        //update_stored_image_uri_path(picturePath); // it doesnt work
        Log.d("uploadimage str_path",picturePath);
        global_picture_path = picturePath;

        _imageFileName = String.valueOf(System.currentTimeMillis());

      //  update_stored_image_uri_path(picturePath);
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
          //  resultText = (TextView) findViewById(R.id.textView); //bcz this is bg can't show anything on mainscreen
           //b resultText.setText("New file " + _imageFileName + ".jpg created\n");
            // Displays the progress bar for the first time.
            NotificationManager mnotifyManager = (NotificationManager) globalcontext.getSystemService(globalcontext.NOTIFICATION_SERVICE);
            //mnotifyManager =
              //      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mbuilder = new NotificationCompat.Builder(globalcontext);
            mbuilder.setContentTitle("Picture Upload")
                    .setContentText("Upload in progress")
                    .setSmallIcon(R.drawable.upload);


        }


        @Override
        protected String doInBackground(RequestPackage... params) {
            mbuilder.setProgress(0, 0, true);
            // Displays the progress bar for the first time.
//            mnotifyManager.notify(not_id, mbuilder.build());
            String content = MyHttpURLConnection.getData(params[0]);
            return content;

        }

        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            mbuilder.setContentText("Upload complete")
                    // Removes the progress bar
                    .setProgress(0,0,false);
//            mnotifyManager.notify(not_id, mbuilder.build());





            //one delay option

            //commenting it right now
            int secs = 5; // Delay in seconds

            Utils.delay(secs, new Utils.DelayCallback() {
                @Override
                public void afterDelay() {
                    // Do something after delay



                   //b resultText.setText(result);

                    //here  i shd delete the image path if uploaded after notification click
                    //    list_Unsent_Images.remove(list_Unsent_Images.get());


                  //b  count1++; //becoz starting from zero
                    if(result != null && !result.trim().isEmpty()){
                        //flag = 0; //flag 0 means successfull uploading
                        Toast.makeText(globalcontext,
                                "Finished Uploading Image number : " +  "count1", Toast.LENGTH_SHORT)
                                .show();
                        flag = 0;
                    }
                    else{
                        // flag = 1;//flag = 1 means that image has failed uploading
                        //to push the imagepath to the arraylist
                        //     Log.d("failed image path : ",stored_image_uri_path); cant print out coz screen not available

//                        boolean isinserted = myDb.insertdata(stored_image_uri_path);
                       // Log.d("Data inserted is : ",get_updated_stored_image_uri_path());
                        Log.d("Data inserted is : ",global_unsent_path);
                        Log.d("Data inserted is : ",global_picture_path);

                        //boolean isinserted = myDb.insertdata(get_updated_stored_image_uri_path());
//                        boolean isinserted = myDb.insertdata(global_unsent_path);
                        boolean isinserted = myDb.insertdata(global_picture_path);
                        if(isinserted == true){
                            Toast.makeText(globalcontext, "Data Inserted", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(globalcontext, "Data Not Inserted", Toast.LENGTH_SHORT).show();
                        }

                        flag = 0;

                        //Building up the notification for it.
                     /*   NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(globalcontext)
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
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());*/



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

                }
            });

        }
    }


}
package com.highbryds.fitfinder.commonHelper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.highbryds.fitfinder.callbacks.videoCompressionCallback;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

public class JavaHelper {

    static  videoCompressionCallback callback;

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("KeyHASH", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHASH", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("KeyHASH", "printHashKey()", e);
        }
    }

    public static void compress(String videoPath , Context context , videoCompressionCallback videoCompressionCallback){
        callback = videoCompressionCallback;
        File f = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + "/FitFinder/");
        new VideoCompress(context).execute(videoPath, f.getPath());
    }

    public static String getAddress(Context context, Double lat , Double lng){
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            return address;
        }catch (Exception e){
            return "";
        }
    }

    public static class VideoCompress extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompress(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return filePath;

        }

        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File CompressedVideo = new File(compressedFilePath);
            callback.isCompress(true , CompressedVideo.getPath());
        }
    }

    public static String CompressPic(File file , Context context) throws IOException {

        File compressedImageFile = new Compressor(context).compressToFile(file);
        return compressedImageFile.getAbsolutePath();

    }


    public static String badWordReplace(String input){
        for (String word : JavaBadWordList.words) {
            Pattern rx = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
            input = rx.matcher(input).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
        }

        return input;
    }

}

package com.application.material.takeacoffee.app.singletons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by davide on 03/01/15.
 */
public class ImagePickerSingleton {
    private static ImagePickerSingleton imagePickerInstance;
    private static Context context;
    public final static int PICK_PHOTO_CODE = 99;
    private final int thumbnailSize = 500;
    private String pictureUrl;

    private ImagePickerSingleton() {
    }

    public static ImagePickerSingleton getInstance(Context ctx) {
        context = ctx;
        return imagePickerInstance == null ?
                imagePickerInstance = new ImagePickerSingleton() :
                imagePickerInstance;
    }

    public final String APP_TAG = "TakeACoffeeApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "picture1.jpg";
    public String photoFileName2 = "picture2.jpg";

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name
        // Start the image capture intent to take photo
        ((Activity) context).startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public Bitmap onActivityResultWrapped(int requestCode,
                                        int resultCode,
                                        Intent data) throws Exception {
        //from camera
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                pictureUrl = takenPhotoUri.getPath();

                // by this point we have the camera photo on disk
//                Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                Bitmap takenImage = BitmapFlipper.rotateBitmapOrientation(takenPhotoUri.getPath());
                // Load the taken image into a preview
                //set thumbnail
                return getThumbnail(takenImage);
            } else { // Result was a failure
                Toast.makeText(context, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

        //from gallery
        if (data != null) {
            Uri photoUri = data.getData();
            File file = getFileFromContentUri(photoUri);

            pictureUrl = file.getPath();
            Bitmap selectedImage = BitmapFlipper.rotateBitmapOrientation(file.getPath());
            // Load the selected image into a preview
            return getThumbnail(selectedImage);
        }

        return null;
    }

    private File getFileFromContentUri(Uri photoUri) throws IOException {
        InputStream is = null;
        FileOutputStream os = null;
        File file = getPhotoFile(photoFileName2);
        try {
            is = context.getContentResolver().openInputStream(photoUri);
            os = new FileOutputStream(file);
            byte [] buffer = new byte[1024];
            int read;
            while((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                is.close();
            }
            if(os != null) {
                os.close();
            }
        }
        return file;
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists() && ! mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
    }

    public File getPhotoFile(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists() && ! mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }


    // Trigger gallery selection for a photo
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Bring up gallery to select a photo
        ((Activity) context).startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    //createThumbnail
    public Bitmap getThumbnail(Bitmap pic) {
        return BitmapScaler.scaleToFitHeight(pic, thumbnailSize);
    }

    public Bitmap getRoundedPicture(Bitmap pic) {
        return BitmapRounder.roundBitmap(pic, pic.getHeight());
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public static class BitmapScaler {
        // Scale and maintain aspect ratio given a desired width
        // BitmapScaler.scaleToFitWidth(bitmap, 100);
        public static Bitmap scaleToFitWidth(Bitmap b, int width) {
            float factor = width / (float) b.getWidth();
            return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
        }


        // Scale and maintain aspect ratio given a desired height
        // BitmapScaler.scaleToFitHeight(bitmap, 100);
        public static Bitmap scaleToFitHeight(Bitmap b, int height) {
            float factor = height / (float) b.getHeight();
            return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
        }

    }

    public static class BitmapFlipper {
        public static Bitmap rotateBitmapOrientation(String photoFilePath) throws IOException, Exception {
            // Create and configure BitmapFactory
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photoFilePath, bounds);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
            // Read EXIF Data
            ExifInterface exif = new ExifInterface(photoFilePath);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
            // Rotate Bitmap
            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
            // Return result
            return rotatedBitmap;
        }
    }

    public static class BitmapRounder {
        public static Bitmap roundBitmap(Bitmap bitmap, int size) {
            Bitmap result;
            if(bitmap != null) {
                try {
                    bitmap = BitmapScaler.scaleToFitWidth(bitmap, size); //NOT CHECKING ERROR COS IF NOT RETURN NOT CHANGED BITMAP
                    result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(result);

                    int color = 0xff424242;
                    Paint paint = new Paint();

                    paint.setAntiAlias(true);
                    canvas.drawARGB(0, 0, 0, 0);
                    paint.setColor(color);
                    canvas.drawCircle(size / 2, size / 2, size / 2, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(bitmap, 0, 0, paint);
                    return result;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError o) {
                    o.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

    }
}

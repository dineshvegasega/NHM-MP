package com.nhm.distributor.abc;

public class aajva {

//    private void openCameraIntent() {
//        Intent pictureIntent = new Intent(
//                MediaStore.ACTION_IMAGE_CAPTURE);
//        if(pictureIntent.resolveActivity(getPackageManager()) != null){
//            //Create a file to store the image
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            ...
//            }
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,                                                                                                    "com.example.android.provider", photoFile);
//                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        photoURI);
//                startActivityForResult(pictureIntent,
//                        REQUEST_CAPTURE_IMAGE);
//            }
//        }
//    }

//    Uri uri = data.getData();
//    ContentResolver cr = this.getContentResolver();
//    String mime = cr.getType(uri);

//
//String fileName = "myImage";//no .png or .jpg needed
//    try {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
//        fo.write(bytes.toByteArray());
//        // remember close file output
//        fo.close();
//    } catch (Exception e) {
//        e.printStackTrace();
//        fileName = null;
//    }

}

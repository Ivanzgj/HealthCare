package com.ivan.healthcare.healthcare_android.settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.ui.BaseActivity;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 裁剪图片的页面
 * Created by Ivan on 16/4/12.
 */
public class CropImageActivity extends BaseActivity {

    private static final int SAVE_CROP_RESULT_ID = 0x31;
    private static final int CANCEL_CROP_RESULT_ID = 0x32;
    public static final String CROPPED_BITMAP = "bitmap";

    private CropImageView mCropImageView;
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        imageUri = intent.getData();

        initView();
    }

    private void initView() {
        View rootView = View.inflate(this, R.layout.activity_cropimage, null);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.crop_toolbar);
        toolbar.setTitle(R.string.crop_title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCropImageView = (CropImageView) rootView.findViewById(R.id.crop_image_cropper);
        mCropImageView.setImageUriAsync(imageUri);

        setContentView(rootView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, SAVE_CROP_RESULT_ID, 1, R.string.crop_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, CANCEL_CROP_RESULT_ID, 2, R.string.cancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case SAVE_CROP_RESULT_ID:
                Intent data = new Intent();
                Bitmap bm = mCropImageView.getCroppedImage();
                Matrix matrix = new Matrix();
                int targetSize = getResources().getDimensionPixelSize(R.dimen.crop_image_size);
                int width = bm.getWidth();
                int height = bm.getHeight();
                int size = width>height?width:height;
                float scale = (float)targetSize / (float)size;
                matrix.postScale(scale, scale);
                bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

                String home = getFilesDir().getAbsolutePath();
                File userDir = new File(home + Configurations.USER_DIR);
                if (!userDir.exists()) {
                    if (!userDir.mkdir()) {
                        bm.recycle();
                        setResult(RESULT_CANCELED);
                        finish();
                        break;
                    }
                }
                File avatarFile = new File(home + Configurations.AVATAR_FILE_PATH);
                try {
                    if (!avatarFile.exists()) {
                        if (!avatarFile.createNewFile()) {
                            bm.recycle();
                            setResult(RESULT_CANCELED);
                            finish();
                            break;
                        }
                    }
                    BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(avatarFile));
                    bm.compress(Bitmap.CompressFormat.PNG, 100, os);  //图片存成png格式。
                    os.close();
                } catch (IOException e) {
                    bm.recycle();
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
                }

                data.putExtra(CROPPED_BITMAP, bm);
                setResult(RESULT_OK, data);
                finish();
                bm.recycle();
                break;
            case CANCEL_CROP_RESULT_ID:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

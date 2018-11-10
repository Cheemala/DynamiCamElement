package cheemala.projects.dynamicamera;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import static android.Manifest.permission.CAMERA;

public class HomeActivity extends AppCompatActivity {

    public static int CAM_PERMISSION_REQUEST_CODE = 111;
    public static int CAM_REQUEST_CODE = 222;
    private LinearLayout dynamicItmHolder;
    private LayoutInflater inflater;
    private String currntImgTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeViews();
        adDynamicItem();

        if (!checkPermission()) {
            requestPermission();
        }
    }

    private void initializeViews() {

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dynamicItmHolder = findViewById(R.id.dynamic_itm_holder);

    }

    private void adDynamicItem() {

        final View itemView = inflater.inflate(R.layout.dynamic_cam_item, null);
        ImageView imgVw = itemView.findViewById(R.id.img_vw);
        imgVw.setTag(getImgTagIdntfer(dynamicItmHolder));
        Button clkBtn = itemView.findViewById(R.id.clk_btn);
        clkBtn.setTag(getCamBtnTagIdntfer(dynamicItmHolder));
        Button adBtn = itemView.findViewById(R.id.ad_btn);
        adBtn.setTag(getAdBtnTagIdntfer(dynamicItmHolder));
        adBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itmTag = (String) view.getTag();
                Button clkdBtnView = dynamicItmHolder.findViewWithTag(itmTag);
                if (clkdBtnView.getText().toString().contentEquals(getString(R.string.ad_clk))) {
                    adDynamicItem();
                    clkdBtnView.setText(getString(R.string.remov_clk));
                } else if (clkdBtnView.getText().toString().contentEquals(getString(R.string.remov_clk))) {
                    int itemPos = Integer.parseInt(itmTag.substring(6));
                    removDynamicItem(itemPos);
                } else {
                }

            }
        });

        clkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String crnBtnTag = (String) view.getTag();
                currntImgTag = "img" + crnBtnTag.substring(7);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAM_REQUEST_CODE);

            }
        });

        dynamicItmHolder.addView(itemView);

    }

    private void removDynamicItem(int itemPos) {
        View removableItm = dynamicItmHolder.getChildAt((itemPos));
        if (removableItm != null) {
            dynamicItmHolder.removeView(removableItm);
            resetItmTags();
        }
    }

    private void resetItmTags() {
        for (int i = 0; i < dynamicItmHolder.getChildCount(); i++) {
            View itemView = dynamicItmHolder.getChildAt(i);
            ImageView imgVw = itemView.findViewById(R.id.img_vw);
            imgVw.setTag("img" + i);
            Button camBtn = itemView.findViewById(R.id.clk_btn);
            camBtn.setTag("cam_btn" + i);
            Button adBtn = itemView.findViewById(R.id.ad_btn);
            adBtn.setTag("ad_btn" + i);
        }
    }

    private Object getAdBtnTagIdntfer(LinearLayout dynamicItmHolder) {
        return "ad_btn" + (dynamicItmHolder.getChildCount());
    }

    private Object getCamBtnTagIdntfer(LinearLayout dynamicItmHolder) {
        return "cam_btn" + (dynamicItmHolder.getChildCount());
    }

    private Object getImgTagIdntfer(LinearLayout dynamicItmHolder) {
        return "img" + (dynamicItmHolder.getChildCount());
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return (result1 == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, CAM_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAM_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                ImageView dynamicImg = dynamicItmHolder.findViewWithTag(currntImgTag);
                Bitmap bitmapImg = (Bitmap) data.getExtras().get("data");
                dynamicImg.setImageBitmap(bitmapImg);
            }
        } else {
            Toast.makeText(HomeActivity.this, "Image not captured!", Toast.LENGTH_SHORT).show();
        }

    }
}

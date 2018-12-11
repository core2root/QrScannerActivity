import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import com.google.zxing.Result;
import com.outlocks.wikey.base.BaseAppActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
public class QrReaderActivity extends BaseAppActivity implements ZXingScannerView.ResultHandler {

	public static final String TAG  = QrReaderActivity.class.getSimpleName();
	public static final int QR_ACTIVITY = 56;
	public static final String SCAN_RESULT = "scan_result";

	private ZXingScannerView scannerView;
	//code to request camera permission
	public static final int REQUEST_CAMERA = 43;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scannerView = new ZXingScannerView(this);
		setContentView(scannerView);

		scannerView.setResultHandler(this);
		enableToolbarBackBtn();

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode){
			case REQUEST_CAMERA:
				boolean isGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
				if(isGranted){
					startQrCamera();
				}else{
					Toast.makeText(this, "must grant camera permission to use QR reader", Toast.LENGTH_SHORT).show();
				}
				break;
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private void askCameraPermission(){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
				startQrCamera();
			}else{
				requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
			}
		}else{
			startQrCamera();
		}
	}

	private void startQrCamera(){
		scannerView.startCamera();
	}

	/**
	 * result from QR scanner activity.
	 * @param result
	 */
	@Override
	public void handleResult(final Result result) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Scan Result");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				scannerView.resumeCameraPreview(QrReaderActivity.this);
				goBackToMainActivityWithResult(result.getText());
			}
		});

		builder.setMessage(result.getText());
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void goBackToMainActivityWithResult(String result){
		Intent intent = new Intent();
		intent.putExtra(SCAN_RESULT, result);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onResume() {
		askCameraPermission();
		super.onResume();
	}

	@Override
	protected void onPause() {
		scannerView.stopCamera();
		super.onPause();
	}
}

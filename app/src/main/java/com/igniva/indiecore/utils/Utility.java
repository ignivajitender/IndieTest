package com.igniva.indiecore.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.widget.Toast;

import com.igniva.indiecore.Manifest;
import com.igniva.indiecore.R;

public class Utility {

	public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	/**
	 * @param context
	 * @return Returns true if there is network connectivity
	 */
	public static boolean isInternetConnection(Context context) {
		boolean HaveConnectedWifi = false;
		boolean HaveConnectedMobile = false;

		try {

			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni != null) {
				if (ni.getType() == ConnectivityManager.TYPE_WIFI)
					if (ni.isConnectedOrConnecting())
						HaveConnectedWifi = true;
				if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
					if (ni.isConnectedOrConnecting())
						HaveConnectedMobile = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return HaveConnectedWifi || HaveConnectedMobile;
	}

	/**
	 * Display Toast Message
	 **/
	public static void showToastMessageShort(Activity context, String message) {
		Toast.makeText(context.getApplicationContext(), message,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Display Toast Message
	 **/
	public static void showToastMessageLong(Activity context, String message) {
		Toast.makeText(context.getApplicationContext(), message,
				Toast.LENGTH_LONG).show();
	}



	/**
	 * Get IP address from first non-localhost interface
	 * 
	 * @param useIPv4
	 *            true=return ipv4, false=return ipv6
	 * @return address or empty string
	 */
	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress();
						// boolean isIPv4 =
						// InetAddressUtils.isIPv4Address(sAddr);
						boolean isIPv4 = sAddr.indexOf(':') < 0;

						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 zone
																// suffix
								return delim < 0 ? sAddr.toUpperCase() : sAddr
										.substring(0, delim).toUpperCase();
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "";
	}

	public void showNoInternetDialog(final Activity mContext) {
		try {

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext,
					R.style.AppCompatAlertDialogStyle);
			builder.setTitle(mContext.getResources().getString(R.string.no_internet_title));
			builder.setMessage(mContext.getResources().getString(R.string.no_internet));
			builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();
					((Activity) mContext).finish();

				}
			});

			builder.show();
		} catch (Exception e) {
			showToastMessageShort(mContext,
					mContext.getResources().getString(R.string.no_internet));
		}
	}

    public void showInvalidSessionDialog(final Activity mContext) {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext,
                    R.style.AppTheme);
            builder.setTitle(mContext.getResources().getString(R.string.invalid_session));
            builder.setMessage(mContext.getResources().getString(R.string.logout_device));
            builder.setPositiveButton("OK", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    // TODO redirect to login screen
                    ((Activity) mContext).finish();

                }
            });

            builder.show();
        } catch (Exception e) {
            showToastMessageLong(mContext,
                    mContext.getResources().getString(R.string.no_internet));
        }
    }


	public static void showAlertDialog(String message, Context context){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		builder1.setMessage( message);
		builder1.setCancelable(true);
		builder1.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();

			}
		});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}
	public static boolean checkPermission(final Context context)
	{
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if(currentAPIVersion>= Build.VERSION_CODES.M)
		{
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("External storage permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes, new OnClickListener() {
						@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
						}
					});
					AlertDialog alert = alertBuilder.create();
					alert.show();
				} else {
					ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

}

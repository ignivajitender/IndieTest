package com.igniva.indiecore.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.Toast;

import com.igniva.indiecore.Manifest;
import com.igniva.indiecore.R;
import com.igniva.indiecore.ui.activities.BoardActivity;
import com.igniva.indiecore.ui.activities.EnterMobileActivity;
import com.igniva.indiecore.ui.activities.InviteContactActivity;

public class Utility {





	public static void checkGPSStatus(final Context context) {
		try {
			LocationManager locationManager = null;
			boolean gps_enabled = false;
			boolean network_enabled = false;
			if ( locationManager == null ) {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            }
			try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex){}
			try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex){}
			if ( !gps_enabled && !network_enabled ){
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("GPS not enabled");
                dialog.setPositiveButton("Ok", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //this will navigate user to the device location settings screen
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
			builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					//((Activity) mContext).finish();
				}
			});
			builder.show();
		} catch (Exception e) {
			showToastMessageShort(mContext,
					mContext.getResources().getString(R.string.no_internet));
		}
	}


    public static void showInvalidSessionDialog(final Activity mContext) {
        try {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext,
					R.style.AppCompatAlertDialogStyle);;
            builder.setTitle(mContext.getResources().getString(R.string.invalid_session));
            builder.setMessage(mContext.getResources().getString(R.string.logout_device));
            builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // TODO redirect to login screen
					((Activity) mContext).startActivity(new Intent(mContext, EnterMobileActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

				}
            });

            builder.show();
        } catch (Exception e) {
            showToastMessageLong(mContext,
                    mContext.getResources().getString(R.string.no_internet));
        }
    }


	public static void showAlertDialog(String message, Context context,String name){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		builder1.setMessage( message);
		builder1.setCancelable(true);
//		builder1.setPositiveButton(android.R.string.ok, new OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
////				new BoardActivity().removePost(postId);
//			}
//		});
		builder1.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

//	public static void showRemovePostAlertDialog(String message, final Context context, final String postId){
//		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
//		builder1.setMessage( message);
//		builder1.setCancelable(true);
//		builder1.setPositiveButton(android.R.string.ok, new OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				new BoardActivity().removePost(context,postId);
//			}
//		});
//		builder1.setNegativeButton(android.R.string.cancel, new OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				dialog.dismiss();
//			}
//		});
//		AlertDialog alert11 = builder1.create();
//		alert11.show();
//	}

	public static void showAlertDialogGetBadge(String message, Context context){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context,R.style.AppCompatAlertDialogStyle);
		builder1.setMessage( message);
		builder1.setCancelable(true);
		builder1.setPositiveButton(android.R.string.ok, new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});

		builder1.setNegativeButton(context.getResources().getString(R.string.no_thanks), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	public static AlertDialog.Builder showAlertDialogOkNoThanks(String message, Context context){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context,R.style.AppCompatAlertDialogStyle);
		builder1.setMessage( message);
		builder1.setCancelable(true);
		return builder1;
	}


	public static void showAlertDialogInviteAndBuy(String message, final Context context){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context,R.style.AppCompatAlertDialogStyle);
		builder1.setMessage( message);
		builder1.setCancelable(true);
		builder1.setPositiveButton("BUY", new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		}); builder1.setNegativeButton("INVITE", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
          Intent intent = new Intent(context, InviteContactActivity.class);
				intent.putExtra(Constants.INDEX,3);
				context.startActivity(intent);
			}
		});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	/**
	 *  Open Payment Dialog, Initiate payment on Ok click
	 *
	 * @param message
	 * @param context
     */
	public static AlertDialog.Builder showAlertDialogBuy(String message, final Context context){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context,R.style.AppCompatAlertDialogStyle);
		builder1.setMessage( message);
		builder1.setCancelable(true);
//		builder1.setPositiveButton("Buy a badge slot", new OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//
//				// start payment
//
//				// close dialog
//				dialog.dismiss();
//
//			}
//		});
//		builder1.setNegativeButton("INVITE", new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Intent intent = new Intent(context, InviteContactActivity.class);
//				intent.putExtra(Constants.INDEX,3);
//				context.startActivity(intent);
//			}
//		});

		//AlertDialog alert11 = builder1.create();
		//alert11.show();
		return builder1;
	}


	public  static String getDateFromUTCTimestamp(long mTimestamp, String mDateFormate) {
		String date = null;
		try {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.setTimeInMillis(mTimestamp * 1000L);
			date = DateFormat.format(mDateFormate, cal.getTimeInMillis()).toString();

			SimpleDateFormat formatter = new SimpleDateFormat(mDateFormate);
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date value = formatter.parse(date);

			SimpleDateFormat dateFormatter = new SimpleDateFormat(mDateFormate);
			dateFormatter.setTimeZone(TimeZone.getDefault());
			date = dateFormatter.format(value);
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

}

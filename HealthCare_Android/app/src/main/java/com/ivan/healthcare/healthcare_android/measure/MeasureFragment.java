package com.ivan.healthcare.healthcare_android.measure;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.MainActivity;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.bluetooth.BluetoothCommUtil;
import com.ivan.healthcare.healthcare_android.util.Compat;
import com.ivan.healthcare.healthcare_android.view.chart.LineChart;
import com.ivan.healthcare.healthcare_android.local.Preference;
import com.ivan.healthcare.healthcare_android.util.L;
import com.ivan.healthcare.healthcare_android.view.CircleProgressView;
import com.ivan.healthcare.healthcare_android.view.chart.provider.SimpleChartAdapter;
import com.ivan.healthcare.healthcare_android.view.material.ButtonFlat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

/**
 * 蓝牙通信的fragment
 * Created by Ivan on 16/1/24.
 */
public class MeasureFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "Bluetooth_Fragment";
    /**
     * 蓝牙通信的service
     */
    private BluetoothCommUtil mService;
    /**
     * 测量进度条
     */
    private CircleProgressView progressView;
    /**
     * 测量曲线图
     */
    private LineChart measureChart;
    /**
     * 显示心率的textview
     */
    private TextView beepTextView;
    /**
     * 控制开始测量和结束测量的textview
     */
    private ButtonFlat measureBtn;
    /**
     * 蓝牙适配器引用
     */
    private BluetoothAdapter btAdapter;
    /**
     * 蓝牙是否启用的标识
     */
    private boolean isBluetoothEnable = false;
    /**
     * 是否正在测量的标识
     */
    private boolean isMeasuring = false;
    /**
     * 监听蓝牙状态的BroadcastReceiver
     */
    private BroadcastReceiver bluetoothStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int curState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            String state="";
            switch (curState) {
                case (BluetoothAdapter.STATE_TURNING_ON):
                    state = "bluetooth turning on";
                    break;
                case (BluetoothAdapter.STATE_TURNING_OFF):
                    state = "bluetooth turning off";
                    break;
                case (BluetoothAdapter.STATE_ON):
                    state = "bluetooth on";
                    isBluetoothEnable = true;
                    measureBtn.setText(getResources().getString(R.string.bt_finding));

                    // 启动蓝牙服务
                    if (btMsgHandler == null) {
                        btMsgHandler = new BluetoothMessageHandler((MainActivity) getActivity());
                    }
                    mService = new BluetoothCommUtil(btAdapter, btMsgHandler);
                    mService.start();

                    // 连接设备
                    connectToBondFirst();

                    break;
                case (BluetoothAdapter.STATE_OFF):
                    state = "bluetooth off";
                    measureBtn.setText(getResources().getString(R.string.measure_text));
                    isBluetoothEnable = false;
                    break;
                default:
                    break;
            }
            L.d(TAG, state);
        }
    };
    /**
     * 监听蓝牙发现设备的BroadcastReceiver
     */
    private BroadcastReceiver bluetoothDiscoveryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceList.add(remoteDevice);
            listDialogAdapter.notifyDataSetChanged();
        }
    };
    /**
     * 监听蓝牙发现开始和结束的BroadcastReceiver
     * 用以更新发现的设备
     */
    private BroadcastReceiver bluetoothDiscoveryMonitor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                L.d(TAG, "start discovery");
                dialog.show();

            } else if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                L.d(TAG, "finish discovery");
            }
        }
    };
    /**
     * 显示蓝牙设备的列表对话框
     */
    private BaseAdapter listDialogAdapter;
    private AlertDialog dialog;
    /**
     * 蓝牙设备列表
     */
    private ArrayList<BluetoothDevice> deviceList;
    /**
     * mainActivity的引用
     */
    private Context context;
    /**
     * 蓝牙消息的handler
     */
    private BluetoothMessageHandler btMsgHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_measure, container, false);
        initConfiguration(rootView);
        registerReceiver();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
        if (mService != null) {
            mService.stop();
        }
        closeBluetooth();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(measureBtn)) {
            // 打开蓝牙
            if (!isBluetoothEnable) {
                openBluetooth();
                return;
            }

            if (isMeasuring){
                // 断开蓝牙连接
                mService.stop();
                isMeasuring = false;
//                closeBluetooth();
            } else {
                // 连接设备
                connectToBondFirst();
            }
        }
    }

    /**
     * 初始化
     */
    private void initConfiguration(View rootView) {

        progressView = (CircleProgressView) rootView.findViewById(R.id.measure_view);
        progressView.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressView.setProgress(0.6f);
            }
        }, 2000);

        measureChart = (LineChart) rootView.findViewById(R.id.measure_chart);
        measureChart.setBackgroundColor(Compat.getColor(getActivity(), R.color.pureWindowBackground));

        // test
        final ArrayList<Float> data = new ArrayList<>();
        data.add(100.f);data.add(50.f);data.add(110.f);data.add(55.f);data.add(102.f);
        data.add(53.f);data.add(100.f);data.add(55.f);data.add(110.f);data.add(54.f);
        measureChart.setAdapter(new SimpleChartAdapter() {

            @Override
            public int getLineCount() {
                return 1;
            }

            @Override
            public ArrayList<Float> getLineData(int index) {
                return data;
            }

            @Override
            public int getLineColorId(int index) {
                return R.color.chart_cyan;
            }

            @Override
            public boolean drawXLabels() {
                return false;
            }

        });

        measureBtn = (ButtonFlat) rootView.findViewById(R.id.beep_measure_btn);
        measureBtn.setOnClickListener(this);

        beepTextView = (TextView) rootView.findViewById(R.id.beep_textview);

        deviceList = new ArrayList<>();

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            measureBtn.setEnabled(false);
            return;
        }
        if (btAdapter.isEnabled()) {
            measureBtn.setText(getResources().getString(R.string.bt_finding));
            isBluetoothEnable = true;

            // 启动蓝牙服务
            if (btMsgHandler == null) {
                btMsgHandler = new BluetoothMessageHandler((MainActivity) getActivity());
            }
            mService = new BluetoothCommUtil(btAdapter, btMsgHandler);
            mService.start();

            // 连接设备
            connectToBondFirst();
        }

        /**
         * test network
         */
//        final Button test = (Button) rootView.findViewById(R.id.post_btn);
//        test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//                File f = new File(getActivity().getFilesDir(), "laucher.png");
//                if (!f.exists()) {
//                    try {
//                        f.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                try {
//                    FileOutputStream fos = new FileOutputStream(f);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                    fos.flush();
//                    fos.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                BaseStringRequest.Builder builder = new BaseStringRequest.Builder();
//                builder.url(Configurations.REQUEST_URL)
//                        .add("uid",-1)
//                        .add("android_version", android.os.Build.VERSION.RELEASE)
//                        .add("device",android.os.Build.MODEL)
//                        .add("name","Ivan")
//                        .add("file", f, OkHttpUtil.MEDIA_TYPE_PNG)
//                        .build()
//                        .post(new AbsBaseRequest.Callback() {
//                            @Override
//                            public void onResponse(final Response response) {
//                                try {
//                                    final String s = response.body().string();
//                                    L.d(TAG, s);
//                                    test.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            AIToast.show(context, s, Toast.LENGTH_LONG);
//                                        }
//                                    });
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(final int errorFlag) {
//
//                            }
//                        });
//            }
//        });
    }

    /**
     * 连接到蓝牙设备
     * @param device 蓝牙设备
     */
    private void connect(BluetoothDevice device) {
        if (mService.connect(device)) {
            measureBtn.setText(context.getResources().getString(R.string.measure_ing_text));
            isMeasuring = true;
            AppContext.getPreference().editor()
                        .putString(Preference.BOND_DEVICE_ADDRESS, device.getAddress())
                        .commit();
        } else {
            measureBtn.setText(context.getResources().getString(R.string.measure_text));
            isMeasuring = false;
            Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 优先连接到已匹配的设备，没有则启动蓝牙发现
     */
    private void connectToBondFirst() {
        // 连接设备
        String bondDevice = AppContext.getPreference().getString(Preference.BOND_DEVICE_ADDRESS, null);
        if (bondDevice == null) {
            // 发现蓝牙设备
            discovery();
        } else {
            // 自动连接匹配设备
            Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                if (device.getAddress().equals(bondDevice)) {
                    connect(device);
                    return;
                }
            }
            // 发现蓝牙设备
            discovery();
        }
    }

    private void registerReceiver() {
        context.registerReceiver(bluetoothStateBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        context.registerReceiver(bluetoothDiscoveryMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        context.registerReceiver(bluetoothDiscoveryMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        context.registerReceiver(bluetoothDiscoveryBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    private void unregisterReceiver() {
        context.unregisterReceiver(bluetoothStateBroadcastReceiver);
        context.unregisterReceiver(bluetoothDiscoveryMonitor);
        context.unregisterReceiver(bluetoothDiscoveryBroadcastReceiver);
    }

    /**
     * 开启蓝牙
     */
    private void openBluetooth() {
        if (!btAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        }
    }

    /**
     * 关闭蓝牙
     */
    private void closeBluetooth() {
        if (btAdapter.isEnabled()) {
            btAdapter.disable();
        }
    }

    /**
     * 发现设备
     */
    private void discovery() {
        if (btAdapter.isEnabled() && !btAdapter.isDiscovering()) {
            createDiscoveryDialog();
            deviceList.clear();
            deviceList.addAll(btAdapter.getBondedDevices());
            btAdapter.startDiscovery();
        }
    }

    /**
     * 发现蓝牙设备时构建弹出的对话框
     */
    private void createDiscoveryDialog() {
        if (listDialogAdapter == null) {
            listDialogAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return deviceList.size();
                }

                @Override
                public Object getItem(int position) {
                    return deviceList.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView tv;
                    if (convertView != null) {
                        tv = (TextView) convertView;
                    } else {
                        tv = new TextView(context);
                    }
                    tv.setText(deviceList.get(position).getName());
                    return tv;
                }
            };
        }
        if (dialog == null) {
            /**
             * 监听蓝牙设备列表的点击项
             */
            DialogInterface.OnClickListener onDeviceSelectedListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btAdapter.cancelDiscovery();
                    connect(deviceList.get(which));
                    dialog.cancel();
                }
            };
            dialog = new AlertDialog.Builder(context)
                    .setTitle("蓝牙设备")
                    .setAdapter(listDialogAdapter, onDeviceSelectedListener)
                    .setCancelable(false)
                    .setNegativeButton(getResources().getString(R.string.stop), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btAdapter.cancelDiscovery();
                        }
                    })
                    .setPositiveButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (btAdapter.isDiscovering()) {
                                btAdapter.cancelDiscovery();
                            }
                            dialog.cancel();
                            measureBtn.setText(getResources().getString(R.string.measure_text));
                            isMeasuring = false;
                        }
                    })
                    .create();
        }
    }

    /**
     * 处理蓝牙通信时发出的信息，包括蓝牙连接状态和蓝牙接收的数据，这些数据存储在message的bundle中。
     * bundle中以BluetoothCommService.DATAorSTATE标识该数据是蓝牙连接状态(false)还是蓝牙接受的数据(true)，
     * 通过Bundle的getBoolean方法获得。
     * 蓝牙连接状态以BluetoothCommService.CONNECT_STATE为键，
     * 其值为BluetoothCommService.DISCONNECT、
     * BluetoothCommUtil.CONNECTED、
     * BluetoothCommUtil.CONNECT_LOST、
     * BluetoothCommUtil.CONNECT_FAIL，
     * 通过Bundle的getInt方法获得。
     * 蓝牙接收的数据以BluetoothCommService.BLUETOOTH_DATA为键，
     * 数据为字节流byte数组，
     * 通过Bundle的getByteArray方法获得。
     * 数据的长度以BluetoothCommService.BLUETOOTH_DATA_LENGTH为键，
     * 通过Bundle的getInt方法获得。
     */
    static class BluetoothMessageHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        BluetoothMessageHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            if (bundle.getBoolean(BluetoothCommUtil.DATAorSTATE)) {
                // 蓝牙接收到的数据
                int length = bundle.getInt(BluetoothCommUtil.BLUETOOTH_DATA_LENGTH);
                byte[] buffer = bundle.getByteArray(BluetoothCommUtil.BLUETOOTH_DATA);
                if (buffer == null) {
                    return;
                }

                String data = new String(buffer,0,length-1);

                L.d("bluetooth_state", data);
            } else {
                // 蓝牙连接状态
                int state = bundle.getInt(BluetoothCommUtil.CONNECT_STATE);

                switch (state) {
                    case BluetoothCommUtil.DISCONNECT:
                        L.d(TAG, "disconnect");
                        break;
                    case BluetoothCommUtil.CONNECTED:
                        L.d(TAG, "connected");
                        break;
                    case BluetoothCommUtil.CONNECT_FAIL:
                        L.d(TAG, "connect fail");
                        break;
                    case BluetoothCommUtil.CONNECT_LOST:
                        L.d(TAG, "connect lost");
                        break;
                    default:
                        break;
                }
            }
        }
    }
}

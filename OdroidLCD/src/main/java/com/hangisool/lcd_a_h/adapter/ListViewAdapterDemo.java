package com.hangisool.lcd_a_h.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hangisool.lcd_a_h.LcdActivity;
import com.hangisool.lcd_a_h.R;
import com.hangisool.lcd_a_h.interface_pack.OnItemClickListener;
import com.hangisool.lcd_a_h.listVO.ListVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Handler;

import static com.hangisool.lcd_a_h.LcdActivity.conn_COP;
import static com.hangisool.lcd_a_h.LcdActivity.epOUT_COP;
import static com.hangisool.lcd_a_h.LcdActivity.mContext;
import static com.hangisool.lcd_a_h.LcdActivity.numFloorNames;

public class ListViewAdapterDemo extends BaseAdapter implements OnItemClickListener{
    public boolean isClicked;
    public int call;
    public ArrayList<String> floor_str = new ArrayList<>();
    public String cancel_floor;
    public int up_cnt;
    public int down_cnt;
    TextView set_floor_TV_1 = ((LcdActivity) LcdActivity.mContext).set_floor_TV_1;
    TextView set_floor_TV_2 = ((LcdActivity) LcdActivity.mContext).set_floor_TV_2;
    ArrayList<String> floor_AL = ((LcdActivity) LcdActivity.mContext).floor_AL;
    String floor_tv_str;

    private ArrayList<ListVO> listVO = new ArrayList<ListVO>();
    public ListViewAdapterDemo(){
    }

    @Override
    public int getCount() {
        return listVO.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();

            LayoutInflater layoutInflater = LayoutInflater.from(LcdActivity.mContext);
            convertView = layoutInflater.inflate(R.layout.custom_floor_listview,null);
            holder.clv_title = (TextView)convertView.findViewById(R.id.clv_floorName);
            holder.clv_context = (TextView)convertView.findViewById(R.id.clv_floorDetail);
            holder.clv_back = (LinearLayout) convertView.findViewById(R.id.clv_back);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }


        if (isClicked) {
            convertView.callOnClick();
        }

        // 터치시 색상 해야됨
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListVO list = new ListVO();

                int floor_max = up_cnt + down_cnt;

                Log.e("(isClicked)", String.valueOf(floor_str));
                // 텐키 입력
                if (isClicked) {
                    String[] floors = floor_str.toString().split(", ");
                    if (!floor_str.equals("")) {
                        // b8, b1
                        if (call == 1) {
                            // 층 등록 시
                            for (int i = 0; i < floors.length; i++) {
                                if (floor_str.get(i).substring(0, 1).equals("B")) {
                                    int down_num = Integer.parseInt(floor_str.get(i).substring(1, 2));
                                    if (down_num == 1) listVO.get(floor_max - 8).setTouched(true);
                                    if (down_num == 2) listVO.get(floor_max - 7).setTouched(true);
                                    if (down_num == 3) listVO.get(floor_max - 6).setTouched(true);
                                    if (down_num == 4) listVO.get(floor_max - 5).setTouched(true);
                                    if (down_num == 5) listVO.get(floor_max - 4).setTouched(true);
                                    if (down_num == 6) listVO.get(floor_max - 3).setTouched(true);
                                    if (down_num == 7) listVO.get(floor_max - 2).setTouched(true);
                                    if (down_num == 8) listVO.get(floor_max - 1).setTouched(true);
                                } else {
                                    listVO.get(floor_max - (down_cnt + Integer.parseInt(floor_str.get(i).replace("층", "")))).setTouched(true);
                                }
                                v.findViewById(R.id.clv_back).setBackgroundColor(Color.parseColor("#e38c04"));
                            }
                        } else {
                            // 층 취소 시
                                if (cancel_floor.substring(0, 1).equals("B")) {
                                    int down_num = Integer.parseInt(cancel_floor.substring(1, 2));
                                    if (down_num == 1) listVO.get(floor_max - 8).setTouched(false);
                                    if (down_num == 2) listVO.get(floor_max - 7).setTouched(false);
                                    if (down_num == 3) listVO.get(floor_max - 6).setTouched(false);
                                    if (down_num == 4) listVO.get(floor_max - 5).setTouched(false);
                                    if (down_num == 5) listVO.get(floor_max - 4).setTouched(false);
                                    if (down_num == 6) listVO.get(floor_max - 3).setTouched(false);
                                    if (down_num == 7) listVO.get(floor_max - 2).setTouched(false);
                                    if (down_num == 8) listVO.get(floor_max - 1).setTouched(false);
                                } else {
                                    listVO.get(floor_max - (down_cnt + Integer.parseInt(cancel_floor))).setTouched(false);
                                }
                                v.findViewById(R.id.clv_back).setBackgroundColor(Color.parseColor("#00000000"));
                        }
                    }
                }

                // 터치 시
                if (!isClicked) {

                    Log.e("(up_cnt - position)", String.valueOf(up_cnt));
                    Log.e("(down_cnt - position)", String.valueOf(down_cnt));

                    if (up_cnt <= position) {
                         // 지하층
                        setFloor_TV(up_cnt, listVO.get(position).getTitle());
                    } else {
                        setFloor_TV(down_cnt, listVO.get(position).getTitle().replace("F", ""));
                        // 지상층
                    }
//
//                    if ( floor_AL.contains((up_cnt - position) + "층")) {
//                        floor_AL.remove((up_cnt - position) + "층");
//                        Log.e("(up_cnt - position)","sdfsdfsdf");
//
//                        floor_tv_str = floor_AL.toString();
//                        floor_tv_str = floor_tv_str.substring(1, floor_tv_str.length() - 1);
//                    } else {
//                        floor_AL.add(0, (up_cnt - position) + "층");
//                        Log.e("(up_cnt - position)", "Adadad");
//
//                        floor_tv_str = floor_AL.toString();
//                        floor_tv_str = floor_tv_str.substring(1, floor_tv_str.length() - 1);
//                    }
//
//                    set_floor_TV_1.setText(floor_tv_str);
//                    set_floor_TV_2.setText(floor_tv_str);

                    if (!(listVO.get(position).getTouched())) {
                        set_floor_TV_1.setText(floor_tv_str);
                        set_floor_TV_2.setText(floor_tv_str);
                        //리스트뷰의 객체가 터치 되지 않았을때 터치하면 그 객체의 isTouched변수를 true로 만들고 배경색 변경
                        if (!(listVO.get(position).getTouched())) {
                            listVO.get(position).setTouched(true);
                            v.findViewById(R.id.clv_back).setBackgroundColor(Color.parseColor("#e38c04"));
                        }
                    } else {
                        //리스트뷰의 객체가 터치 되었을때 터치하면 그 객체의 isTouched변수를 false로 만들고 배경색 변경
                        if (listVO.get(position).getTouched()) {
                            listVO.get(position).setTouched(false);
                            v.findViewById(R.id.clv_back).setBackgroundColor(Color.parseColor("#00000000"));
                        }
                    }
                }

                isClicked = false;
            }
        });
        //스크롤뷰를 조작했을때 화면상에 안보이던 리스트뷰의 객체들이 다시 그려지게 되므로 기존에 isTouched변수를 읽어 배경색을 무엇으로 할지 결정
        if(listVO.get(position).getTouched()){
            holder.clv_back.setBackgroundColor(Color.parseColor("#e38c04"));
        }else{
            holder.clv_back.setBackgroundColor(Color.parseColor("#00000000"));
        }
        holder.clv_title.setText(listVO.get(position).getTitle());
        holder.clv_context.setText(listVO.get(position).getContext());

        return convertView;
    }

    private void setFloor_TV(int set_cnt, String position) {
        if ( floor_AL.contains( position + "층")) {
            floor_AL.remove(position + "층");
            Log.e("(set_cnt - position)","sdfsdfsdf");

            floor_tv_str = floor_AL.toString();
            floor_tv_str = floor_tv_str.substring(1, floor_tv_str.length() - 1);
        } else {
            floor_AL.add(0, position + "층");

            floor_tv_str = floor_AL.toString();
            floor_tv_str = floor_tv_str.substring(1, floor_tv_str.length() - 1);
        }

        set_floor_TV_1.setText(floor_tv_str);
        set_floor_TV_2.setText(floor_tv_str);
    }

    public void addVO(String title, String desc){
        ListVO item = new ListVO();

        item.setTitle(title);
        item.setContext(desc);

        listVO.add(item);
    }

    @Override
    public void onItemClickListener(View v, int position) {

    }

    static class ViewHolder {
        TextView clv_title;
        TextView clv_context;
        LinearLayout clv_back;
    }
    public ArrayList<ListVO> getListVO(){
        return listVO;
    }

    private void sendPacketThread(final int position){
        Thread sendPacket = new Thread(new Runnable() {
            @Override
            //층 등록 패킷 송신
            public void run() {
                //리스트뷰의 객체중 제일 위에 있는 객체의 position이 0이므로 최상층이 0position을 갖기에 이 인덱스를 반대로해줘야한다.
                //최하층 리스트뷰 객체를 터치 했을경우 position이 최고층수 ex)34 와 같지만 패킷을 송신할때는 0을 송신해야한다.
                byte addr = (byte) (((numFloorNames)-position) / 8);
                byte data = (byte) ((0x80)>>(8 - (((numFloorNames)-position) % 8)));
                byte[] buffer = new byte[6];
                buffer[0] = (byte) 0x02;//STX 0x02
                buffer[1] = (byte) addr;//Addr(0~0xF (0x0=1~8층, 0x1=9~16층,..., 0xF=121~128층)
                buffer[2] = (byte) data;//Data(Bit별 등록 데이터 (0x00, 0x01, 0x02, 0x04, 0x10, 0x20, 0x40, 0x80). 1개층에 대한 정보만 보낸다)
                buffer[3] = (byte) addr;//Addr(0~0xF (0x0=1~8층, 0x1=9~16층,..., 0xF=121~128층)
                buffer[4] = (byte) data;//Data(Bit별 등록 데이터 (0x00, 0x01, 0x02, 0x04, 0x10, 0x20, 0x40, 0x80). 1개층에 대한 정보만 보낸다)
                buffer[5] = (byte) 0x03;//ETX 0x03
                conn_COP.bulkTransfer(epOUT_COP, buffer, 6, 0);
                //50msec 대기
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                buffer[2] = 0x00;
                buffer[4] = 0x00;
                conn_COP.bulkTransfer(epOUT_COP, buffer, 6, 0);
            }
        });
        if(epOUT_COP != null){
            sendPacket.start();
        }else{
            Toast.makeText(mContext, "------------------------------------------------USB시리얼연결상태를 확인해주세요!",Toast.LENGTH_LONG).show();
        }
    }
}

package com.andreakim.concertconcierge;

import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by spoorthi on 7/25/16.
 */
public class ConcertAdapter extends RecyclerView.Adapter<ConcertAdapter.ViewHolder> {

    int pos;
    ViewHolder hold;

    private ArrayList<Concert> concerts;
    private static RecyclerViewClickListener mListener;
    public ConcertAdapter(ArrayList<Concert> concerts, RecyclerViewClickListener itemClickListener){
        this.concerts=concerts;
        this.mListener=itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_rows,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        pos = position;
        hold = holder;

        holder.tv_name_artist.setText(concerts.get(position).getName());
        holder.tv_time.setText(concerts.get(position).getTime());
      //  holder.tv_venue.setText(concerts.get(position).getVenue());
        holder.tv_dist.setText(String.valueOf(concerts.get(position).getDist())+"mi");
        holder.tv_city.setText(concerts.get(position).getCity());



       new ImageAsync().execute();

       // holder.tv_image.setImageBitmap(concerts.get(position).getImage());
    }

    private class ImageAsync extends AsyncTask<Void,Void,Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Picasso.with(concerts.get(pos).getContext()).load(concerts.get(pos).getImage_url()).error(R.drawable.concert_black).resize(360,390).centerCrop().into(hold.tv_image);
        }
    }

    @Override
    public int getItemCount() {
        return concerts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_name_artist, tv_time, tv_venue,tv_city,tv_dist;
        private ImageView tv_image;
        private CardView cardView;

        @Override
        public void onClick(View view) {
            mListener.recyclerViewListClicked(view,getLayoutPosition());
        }

        public ViewHolder(View view) {
            super(view);

            tv_name_artist = (TextView)view.findViewById(R.id.concert_artist);
            tv_time = (TextView)view.findViewById(R.id.concert_time);
          //  tv_venue = (TextView)view.findViewById(R.id.concert_venue);
            tv_dist = (TextView)view.findViewById(R.id.txt_distance);
            tv_city=(TextView)view.findViewById(R.id.concert_city);
            tv_image = (ImageView)view.findViewById(R.id.concert_image);
            cardView = (CardView)view.findViewById(R.id.card_item);
            cardView.setOnClickListener(this);

        }
    }
}

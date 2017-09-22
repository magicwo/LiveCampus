package com.uestc.magicwo.livecampus.utils;

import android.content.Context;
import android.media.Image;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.net.Urls;
import com.uestc.magicwo.livecampus.user.UserInfoActivity;

/**
 * Created by Magicwo on 2017/9/22.
 */

public class ImageLoader {

    public static void load(Context context, ImageView imageView, String url) {

        Glide.with(context).load(url).placeholder(R.drawable.default_head).into(imageView);


    }

    /**
     * 不缓存的机制
     *
     * @param context
     * @param imageView
     * @param url
     */
    public static void loadWithoutCache(Context context, ImageView imageView, String url) {

        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);


    }
}

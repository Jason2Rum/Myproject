package com.example.myproject.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.myproject.HomepageActivity;
import com.example.myproject.MyApp;
import com.example.myproject.ProductDetailActivity;
import com.example.myproject.R;
import com.example.myproject.adapter.BrvahAdapter;
import com.example.myproject.adapter.SpaceItem;
import com.example.myproject.bean.BannerBean;
import com.example.myproject.bean.ProductBean;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    View view;
    String getUrl;
    String inforUrl;
    String infor;
    String searchUrl;
    String catalog = "";
    int i;
    boolean isFirst = false;

    CircleImageView entranceAvatar;

    List<String> imgesUrl = new ArrayList<>();
    List<String> imgesID = new ArrayList<>();
    List<String> ID = new ArrayList<>();

    Banner banner;
    RecyclerView product;
    BrvahAdapter madapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    NestedScrollView nestedScrollView;
    List<BannerBean.bannerBean> bannerBeans = new ArrayList<>();
    SearchFragment searchFragment = new SearchFragment();
    CatalogFragment catalogFragment = new CatalogFragment();
    Bundle bundle = new Bundle();

    @InjectView(R.id.Phone)
    ImageView Phone;
    @InjectView(R.id.Live)
    ImageView Live;
    @InjectView(R.id.Pc)
    ImageView Pc;
    @InjectView(R.id.Bag)
    ImageView Bag;
    @InjectView(R.id.Toys)
    ImageView Toys;
    @InjectView(R.id.Cosmetics)
    ImageView Cosmetics;
    @InjectView(R.id.Luxury)
    ImageView Luxury;
    @InjectView(R.id.Gold)
    ImageView Gold;
    @InjectView(R.id.Vt)
    ImageView Vt;
    @InjectView(R.id.search)
    SearchView search;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        banner = view.findViewById(R.id.banner);
        entranceAvatar = view.findViewById(R.id.head_image);

        File file = new File(Environment.getExternalStorageDirectory(), MyApp.AVATAR_FILE_NAME);
        if (file.exists()) {
            entranceAvatar.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + MyApp.AVATAR_FILE_NAME));
        }
            entranceAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomepageActivity homepageActivity = (HomepageActivity) getActivity();
                homepageActivity.openMenu();
            }
        });

        product = view.findViewById(R.id.product);
        product.setNestedScrollingEnabled(false);
        nestedScrollView = view.findViewById(R.id.nest_sv);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        SpaceItem spaceItem = new SpaceItem(35);
        product.addItemDecoration(spaceItem);
        madapter = new BrvahAdapter(R.layout.layout_product, bannerBeans, getActivity());
        product.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        product.setAdapter(madapter);

        madapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OkGo.<String>post(infor)
                        .params("ID", ID.get(position))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                ProductBean productBean = new Gson().fromJson(response.body(), ProductBean.class);
                                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                                intent.putExtra("productBean", productBean);
                                startActivity(intent);

                            }
                        });
            }
        });

        infor = "http://120.79.87.68:5000/getProduct";
        inforUrl = "http://120.79.87.68:5000/getPageProduct";
        getUrl = "http://120.79.87.68:5000/getBanner";
        searchUrl= "http://120.79.87.68:5000/search";

        if (!isFirst) {
            i = 0;
            initBanner();
            initProduct();
            isFirst = true;
        }
        else {
            imgesUrl.clear();
            imgesID.clear();
            initBanner();
        }


        banner.setDelayTime(3000);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load((String)path).into(imageView);
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        i = 0;
                        bannerBeans.clear();
                        initProduct();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (nestedScrollView.getChildAt(0).getMeasuredHeight() - nestedScrollView.getMeasuredHeight())) {
                    i = i + 1;
                    initProduct();

                }
            }
        });
        ButterKnife.inject(this, view);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                OkGo.<String>post(searchUrl)
                        .params("key_words",s)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                BannerBean bannerBean = new Gson().fromJson(response.body(),BannerBean.class);
                                if(bannerBean.getCode().equals("1")){
                                    bundle.putSerializable("search_banner", bannerBean);
                                    searchFragment.setArguments(bundle);
                                    ((HomepageActivity)getActivity()).HomeReplace(searchFragment);
                                }
                                else {
                                    ToastUtils.showShort("无检索内容！");
                                    return;
                                }
                            }
                        });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return view;
    }

    private void initProduct() {
        OkGo.<String>post(inforUrl)
                .params("page", i)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        BannerBean showproduct = new Gson().fromJson(response.body(), BannerBean.class);
                        if (showproduct.getData() == null) {
                            ToastUtils.showShort("没有更多数据了");
                            return;

                        }
                        bannerBeans.addAll(showproduct.getData());
                        madapter.setNewData(bannerBeans);
                        for (BannerBean.bannerBean pelement : showproduct.getData()) {
                            ID.add(pelement.getID());
                        }
                    }
                });


    }

    private void initBanner() {
        OkGo.<String>post(getUrl)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtils.i(response.body());
                        BannerBean bannerbean = new Gson().fromJson(response.body(), BannerBean.class);
                        if (bannerbean == null) {
                            return;
                        }
                        if (bannerbean.getData() != null && bannerbean.getData().size() > 0) {
                            for (BannerBean.bannerBean element : bannerbean.getData()) {
                                imgesUrl.add(element.getPicture());
                                imgesID.add(element.getID());
                            }
                            banner.setImages(imgesUrl)
                                    .setOnBannerListener(new OnBannerListener() {
                                        @Override
                                        public void OnBannerClick(int position) {
                                            OkGo.<String>post(infor)
                                                    .params("ID", imgesID.get(position))
                                                    .execute(new StringCallback() {
                                                        @Override
                                                        public void onSuccess(Response<String> response) {
                                                            ProductBean productBean = new Gson().fromJson(response.body(), ProductBean.class);
                                                            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                                                            intent.putExtra("productBean", productBean);
                                                            startActivity(intent);
                                                        }
                                                    });
                                        }
                                    })
                                    .start();
                        }

                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.Phone, R.id.Live, R.id.Pc, R.id.Bag, R.id.Toys, R.id.Cosmetics, R.id.Luxury, R.id.Gold, R.id.Vt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Phone:
                catalog = "phone";
                break;
            case R.id.Live:
                catalog = "live";
                break;
            case R.id.Pc:
                catalog = "pc";
                break;
            case R.id.Bag:
                catalog = "bag";
                break;
            case R.id.Toys:
                catalog = "toys";
                break;
            case R.id.Cosmetics:
                catalog = "cosmetics";
                break;
            case R.id.Luxury:
                catalog = "luxury";
                break;
            case R.id.Gold:
                catalog = "gold";
                break;
            case R.id.Vt:
                catalog = "vt";
                break;
        }
        OkGo.<String>post(infor)
                .params("catalog",catalog)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        BannerBean bannerBean = new Gson().fromJson(response.body(),BannerBean.class);
                        if(bannerBean.getCode().equals("1")){
                            bundle.putSerializable("catalog_banner", bannerBean);
                            catalogFragment.setArguments(bundle);
                            ((HomepageActivity)getActivity()).HomeReplace(catalogFragment);
                        }
                        else {
                            ToastUtils.showShort("暂无此类别商品！");
                            return;
                        }
                    }
                });
    }


    public void setEntranceAvatar(Bitmap bitmap) {
        entranceAvatar.setImageBitmap(bitmap);

    }
}

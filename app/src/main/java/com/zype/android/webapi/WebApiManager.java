package com.zype.android.webapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.zype.android.BuildConfig;
import com.zype.android.R;
import com.zype.android.ZypeSettings;
import com.zype.android.core.bus.EventBus;
import com.zype.android.core.events.AuthorizationErrorEvent;
import com.zype.android.core.events.ForbiddenErrorEvent;
import com.zype.android.core.events.UnrsolvedHostErrorEvent;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.webapi.builder.AuthParamsBuilder;
import com.zype.android.webapi.builder.DownloadAudioParamsBuilder;
import com.zype.android.webapi.builder.DownloadVideoParamsBuilder;
import com.zype.android.webapi.builder.EntitlementParamsBuilder;
import com.zype.android.webapi.builder.FavoriteParamsBuilder;
import com.zype.android.webapi.builder.ParamsBuilder;
import com.zype.android.webapi.builder.PlayerParamsBuilder;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.events.BaseEvent;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.auth.AccessTokenInfoEvent;
import com.zype.android.webapi.events.auth.RefreshAccessTokenEvent;
import com.zype.android.webapi.events.auth.RetrieveAccessTokenEvent;
import com.zype.android.webapi.events.category.CategoryEvent;
import com.zype.android.webapi.events.consumer.ConsumerEvent;
import com.zype.android.webapi.events.consumer.ConsumerFavoriteVideoEvent;
import com.zype.android.webapi.events.download.DownloadAudioEvent;
import com.zype.android.webapi.events.download.DownloadVideoEvent;
import com.zype.android.webapi.events.entitlements.VideoEntitlementEvent;
import com.zype.android.webapi.events.entitlements.VideoEntitlementsEvent;
import com.zype.android.webapi.events.favorite.FavoriteEvent;
import com.zype.android.webapi.events.favorite.UnfavoriteEvent;
import com.zype.android.webapi.events.linking.DevicePinEvent;
import com.zype.android.webapi.events.onair.OnAirAudioEvent;
import com.zype.android.webapi.events.onair.OnAirEvent;
import com.zype.android.webapi.events.onair.OnAirVideoEvent;
import com.zype.android.webapi.events.player.PlayerAudioEvent;
import com.zype.android.webapi.events.player.PlayerVideoEvent;
import com.zype.android.webapi.events.playlist.PlaylistEvent;
import com.zype.android.webapi.events.search.SearchEvent;
import com.zype.android.webapi.events.settings.ContentSettingsEvent;
import com.zype.android.webapi.events.settings.LiveStreamSettingsEvent;
import com.zype.android.webapi.events.settings.SettingsEvent;
import com.zype.android.webapi.events.video.RetrieveHighLightVideoEvent;
import com.zype.android.webapi.events.video.RetrieveVideoEvent;
import com.zype.android.webapi.events.zobject.ZObjectEvent;
import com.zype.android.webapi.model.auth.AccessTokenInfoResponse;
import com.zype.android.webapi.model.auth.RefreshAccessToken;
import com.zype.android.webapi.model.auth.RetrieveAccessToken;
import com.zype.android.webapi.model.category.CategoryResponse;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoResponse;
import com.zype.android.webapi.model.consumers.ConsumerResponse;
import com.zype.android.webapi.model.download.DownloadAudioResponse;
import com.zype.android.webapi.model.download.DownloadVideoResponse;
import com.zype.android.webapi.model.entitlements.VideoEntitlementResponse;
import com.zype.android.webapi.model.entitlements.VideoEntitlementsResponse;
import com.zype.android.webapi.model.favorite.FavoriteResponse;
import com.zype.android.webapi.model.favorite.UnfavoriteResponse;
import com.zype.android.webapi.model.linking.DevicePinResponse;
import com.zype.android.webapi.model.onair.OnAirAudioResponse;
import com.zype.android.webapi.model.onair.OnAirResponse;
import com.zype.android.webapi.model.onair.OnAirVideoResponse;
import com.zype.android.webapi.model.player.PlayerAudioResponse;
import com.zype.android.webapi.model.player.PlayerVideoResponse;
import com.zype.android.webapi.model.playlist.PlaylistResponse;
import com.zype.android.webapi.model.search.SearchResponse;
import com.zype.android.webapi.model.settings.ContentSettingsResponse;
import com.zype.android.webapi.model.settings.LiveStreamSettingsResponse;
import com.zype.android.webapi.model.settings.SettingsResponse;
import com.zype.android.webapi.model.video.VideoResponse;
import com.zype.android.webapi.model.zobjects.ZObjectResponse;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

/**
 * @author vasya
 * @version 1
 *          date 6/26/15
 */
public class WebApiManager {

    public static final String APP_KEY = ZypeSettings.APP_KEY;
    public static final String CUSTOM_HEADER_KEY = "User-Agent";
    private static final String TAG = WebApiManager.class.getSimpleName();
    private static final String ENDPOINT_API = "https://api.zype.com";
    private static final String ENDPOINT_PLAYER = "https://player.zype.com";
    private static final String ENDPOINT_LOGIN = "https://login.zype.com";
    public static String CUSTOM_HEADER_VALUE = "Dalvik/2.1.0 (Zype Android; Linux; U; Android 5.0.2; One X Build/LRX22G)";
    private static WebApiManager sInstance;
    private final ZypeApiEndpointInterface mApi;
    private final ZypeApiEndpointInterface mLoginApi;
    private final ZypeApiEndpointInterface mDownloadApi;
    private final ZypeApiEndpointInterface mCookieApi;
    private final Context mContext;
    private EventBus mBus;
    private WorkerHandler mHandler;


    private WebApiManager(Context contextArg) {


        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(new AddCookiesInterceptor());
        okHttpClient.interceptors().add(new ReceivedCookiesInterceptor());
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        okHttpClient.setCookieHandler(cookieManager);
        RestAdapter.LogLevel logLevel;
        if (BuildConfig.DEBUG) {
            logLevel = RestAdapter.LogLevel.FULL;
        } else {
            logLevel = RestAdapter.LogLevel.NONE;
        }
        mBus = new EventBus();
        RestAdapter apiRestAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT_API)
                .setRequestInterceptor(new CustomRequestInterceptor())
                .setLogLevel(logLevel)
                .build();
        mApi = apiRestAdapter.create(ZypeApiEndpointInterface.class);

        RestAdapter loginRestAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT_LOGIN)
                .setRequestInterceptor(new CustomRequestInterceptor())
                .setLogLevel(logLevel)
                .build();
        mLoginApi = loginRestAdapter.create(ZypeApiEndpointInterface.class);

        RestAdapter downloadRestAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT_PLAYER)
                .setRequestInterceptor(new CustomRequestInterceptor())
                .setLogLevel(logLevel)
                .setClient(new OkClient(okHttpClient))
                .build();
        mDownloadApi = downloadRestAdapter.create(ZypeApiEndpointInterface.class);

        RestAdapter cookieRestAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT_PLAYER)
                .setRequestInterceptor(new CustomRequestInterceptor())
                .setLogLevel(logLevel)
                .setClient(new OkClient(okHttpClient))
                .build();
        mCookieApi = cookieRestAdapter.create(ZypeApiEndpointInterface.class);

        mContext = contextArg;


        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mHandler = new WorkerHandler(thread.getLooper());
    }

    public static void create(Context c) {
        if (sInstance != null) {
            throw new IllegalStateException("Already created!");
        }
        sInstance = new WebApiManager(c);
    }

    public static synchronized WebApiManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Call create() first");
        }
        return sInstance;
    }

    public synchronized void subscribe(Object subscriber) {
        mBus.register(subscriber);
    }

    public synchronized void unsubscribe(Object subscriber) {
        try {
            mBus.unregister(subscriber);
        } catch (IllegalArgumentException ex) {
        }
    }

    public void cancelPendingRequests(boolean cancelCurrent) {
        mHandler.cancelAllJobs(cancelCurrent);
    }

    private DataEvent executeJob(Job job) {
        Request request = job.getRequest();
        Bundle args = job.getOptions();
        RequestTicket ticket = job.getTicket();
        HashMap<String, String> pathParams = (HashMap<String, String>) args.getSerializable(ParamsBuilder.PATH_PARAMS);
        HashMap<String, String> postParams = (HashMap<String, String>) args.getSerializable(ParamsBuilder.POST_PARAMS);
        HashMap<String, String> getParams = (HashMap<String, String>) args.getSerializable(ParamsBuilder.GET_PARAMS);
        String videoId;
        String playlistId;
        assert getParams != null;
        assert pathParams != null;

        // Making sure we add the token to the request on the same thread that it is refreshed on.
        // This way we don't have to worry about any timing issues with accessing it and refreshing it at the same time
        String accessToken = "access_token";
        if (getParams.containsKey(accessToken))
            getParams.put(accessToken, SettingsProvider.getInstance().getAccessToken());
        if (pathParams.containsKey(accessToken))
            pathParams.put(accessToken, SettingsProvider.getInstance().getAccessToken());
        if (postParams != null && postParams.containsKey(accessToken))
            postParams.put(accessToken, SettingsProvider.getInstance().getAccessToken());

        switch (request) {
            case AUTH_REFRESH_ACCESS_TOKEN:
                return new RefreshAccessTokenEvent(ticket, new RefreshAccessToken(mApi.authRefreshAccessToken(postParams)));
            case AUTH_RETRIEVE_ACCESS_TOKEN:
                return new RetrieveAccessTokenEvent(ticket, new RetrieveAccessToken(mApi.authRetrieveAccessToken(postParams)));
            case TOKEN_INFO:
                String token = getParams.get(AuthParamsBuilder.ACCESS_TOKEN);
                return new AccessTokenInfoEvent(ticket, new AccessTokenInfoResponse(mApi.getTokenInfo(token)));
            case VIDEO_LATEST_GET:
                return new RetrieveVideoEvent(ticket, new VideoResponse(mApi.getVideoList(getParams)));
            case VIDEO_HIGHLIGHT_GET:
                return new RetrieveHighLightVideoEvent(ticket, new VideoResponse(mApi.getVideoList(getParams)));
            case CONSUMER_FAVORITE_VIDEO_GET:
                return new ConsumerFavoriteVideoEvent(ticket, new ConsumerFavoriteVideoResponse(mApi.getFavoriteVideoList(SettingsProvider.getInstance().getConsumerId(), getParams)));
            case FAVORITE:
                return new FavoriteEvent(ticket, new FavoriteResponse(mApi.setFavoriteVideo(SettingsProvider.getInstance().getConsumerId(), postParams)));
            case UN_FAVORITE:
                String favoriteId = pathParams.get(FavoriteParamsBuilder.FAVORITE_ID);
                videoId = pathParams.get(FavoriteParamsBuilder.VIDEO_ID);
                return new UnfavoriteEvent(ticket, new UnfavoriteResponse(mApi.setUnFavoriteVideo(SettingsProvider.getInstance().getConsumerId(), favoriteId, postParams)), videoId);
            case CONSUMER_CREATE:
                return new ConsumerEvent(ticket, new ConsumerResponse(mApi.createConsumer(getParams, postParams)));
            case CONSUMER_GET:
                return new ConsumerEvent(ticket, new ConsumerResponse(mApi.getConsumer(SettingsProvider.getInstance().getAccessTokenResourceOwnerId(), getParams)));
            case DEVICE_PIN_CREATE:
                return new DevicePinEvent(ticket, args, new DevicePinResponse(mApi.createDevicePin(getParams, "")));
            case DEVICE_PIN_GET:
                return new DevicePinEvent(ticket, args, new DevicePinResponse(mApi.getDevicePin(getParams)));
            case CATEGORY:
                return new CategoryEvent(ticket, new CategoryResponse(mApi.getCategory(getParams)));
            case Z_OBJECT:
                return new ZObjectEvent(ticket, new ZObjectResponse(mApi.getZobject(getParams)));
            case GET_SETTINGS:
                return new SettingsEvent(ticket, new SettingsResponse(mApi.getSettings(getParams)));
            case CONTENT_SETTINGS:
                return new ContentSettingsEvent(ticket, new ContentSettingsResponse(mApi.getContentSettings(getParams)));
            case LIVE_STREAM_SETTINGS:
                return new LiveStreamSettingsEvent(ticket, new LiveStreamSettingsResponse(mApi.getLiveStreamSettings(getParams)));
            case SEARCH:
                return new SearchEvent(ticket, new SearchResponse(mApi.getSearchVideo(getParams)));
            case PLAYER_VIDEO:
                videoId = getParams.get(PlayerParamsBuilder.VIDEO_ID);
                return new PlayerVideoEvent(ticket, new PlayerVideoResponse(mDownloadApi.getVideoPlayer(videoId, getParams)));
            case PLAYER_AUDIO:
                videoId = getParams.get(PlayerParamsBuilder.VIDEO_ID);
                return new PlayerAudioEvent(ticket, new PlayerAudioResponse(mApi.getAudioPlayer(videoId, getParams)));
            case PLAYER_DOWNLOAD_VIDEO:
                videoId = pathParams.get(DownloadVideoParamsBuilder.VIDEO_ID);
                return new DownloadVideoEvent(ticket, new DownloadVideoResponse(mDownloadApi.getDownloadVideo(videoId, getParams)), videoId);
            case PLAYER_DOWNLOAD_AUDIO:
                videoId = pathParams.get(DownloadAudioParamsBuilder.VIDEO_ID);
                return new DownloadAudioEvent(ticket, new DownloadAudioResponse(mDownloadApi.getDownloadAudio(videoId, getParams)), videoId);
            case ON_AIR:
                return new OnAirEvent(ticket, new OnAirResponse(mApi.getOnAir(getParams)));
            case PLAYER_ON_AIR_VIDEO:
                videoId = getParams.get(PlayerParamsBuilder.VIDEO_ID);
                getParams.remove(PlayerParamsBuilder.VIDEO_ID);
                return new OnAirVideoEvent(ticket, new OnAirVideoResponse(mCookieApi.getOnAirVideo(videoId, getParams)));
            case PLAYER_ON_AIR_AUDIO:
                videoId = getParams.get(PlayerParamsBuilder.VIDEO_ID);
                getParams.remove(PlayerParamsBuilder.VIDEO_ID);
                return new OnAirAudioEvent(ticket, new OnAirAudioResponse(mCookieApi.getOnAirAudio(videoId, getParams)));
            case PLAYLIST_GET:
                return new PlaylistEvent(ticket, new PlaylistResponse(mApi.getPlaylists(getParams)));
            case VIDEO_FROM_PLAYLIST:
                if (pathParams.containsKey(VideoParamsBuilder.PLAYLIST_ID)) {
                    playlistId = pathParams.get(VideoParamsBuilder.PLAYLIST_ID);
                    return new RetrieveVideoEvent(ticket, new VideoResponse(mApi.getVideosFromPlaylist(playlistId, getParams)));
                } else {
                    throw new IllegalStateException("VideoParamsBuilder.PLAYLIST_ID can not be null");
                }
            case CHECK_VIDEO_ENTITLEMENT:
                videoId = pathParams.get(EntitlementParamsBuilder.VIDEO_ID);
                return new VideoEntitlementEvent(ticket, args, new VideoEntitlementResponse(mApi.checkVideoEntitlement(videoId, getParams)));
            case VIDEO_ENTITLEMENTS:
                return new VideoEntitlementsEvent(ticket, new VideoEntitlementsResponse(mApi.getVideoEntitlements(getParams)));
            default:
                throw new RuntimeException("Unknown request:" + request);
        }
    }

    public Job executeRequest(Request request, Bundle options) {
        Job job = new Job();
        job.setRequest(request);
        job.setOptions(options);
        RequestTicket ticket = RequestTicket.newInstance();
        job.setTicket(ticket);
        mHandler.runJob(job);
        return job;
    }

    public enum Request {
        AUTH_REFRESH_ACCESS_TOKEN,
        AUTH_RETRIEVE_ACCESS_TOKEN,
        TOKEN_INFO,
        VIDEO_LATEST_GET,
        VIDEO_FROM_PLAYLIST,
        VIDEO_HIGHLIGHT_GET,
        CONSUMER_CREATE,
        CONSUMER_GET,
        DEVICE_PIN_CREATE,
        DEVICE_PIN_GET,
        FAVORITE,
        UN_FAVORITE,
        CONSUMER_FAVORITE_VIDEO_GET,
        RETRIEVE_VIDEO,
        CATEGORY,
        SEARCH,
        Z_OBJECT,
        PLAYER_DOWNLOAD_VIDEO,
        PLAYER_DOWNLOAD_AUDIO,
        PLAYER_VIDEO,
        PLAYER_AUDIO,
        GET_SETTINGS,
        CONTENT_SETTINGS,
        LIVE_STREAM_SETTINGS,
        ON_AIR,
        PLAYER_ON_AIR_VIDEO,
        PLAYER_ON_AIR_AUDIO,
        PLAYLIST_GET,
        CHECK_VIDEO_ENTITLEMENT,
        VIDEO_ENTITLEMENTS
    }

    private static class CustomRequestInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestFacade request) {
            request.addHeader(CUSTOM_HEADER_KEY, CUSTOM_HEADER_VALUE);
        }
    }

    public class Job {
        private Request mRequest;
        private RequestTicket mTicket;
        private boolean mIsCanceled = false;
        private Bundle mOptions;

        public Request getRequest() {
            return mRequest;
        }

        public void setRequest(Request request) {
            mRequest = request;
        }

        public RequestTicket getTicket() {
            return mTicket;
        }

        public void setTicket(RequestTicket ticket) {
            mTicket = ticket;
        }

        public synchronized void cancel() {
            mIsCanceled = true;
        }

        public synchronized boolean isCanceled() {
            return mIsCanceled;
        }

        public Bundle getOptions() {
            return mOptions;
        }

        public void setOptions(Bundle options) {
            mOptions = options;
        }
    }

    public class WorkerHandler extends Handler {

        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int UNRESOLVED_HOST = 0;
        private static final int MSG_DO_JOB = 0;
        private Job mCurrentJob;

        public WorkerHandler(Looper lopper) {
            super(lopper);
        }

        @Override
        public void handleMessage(Message msg) {
            Job job = (Job) msg.obj;
            if (job.isCanceled()) {
                // job was canceled before execution
                // there is no sense to run it
                return;
            }
            mCurrentJob = job;
            // perform request
            BaseEvent data = execute(job);
            synchronized (this) {
                // check if job was canceled during execution
                if (!job.isCanceled()) {
                    // We are handling refresh access token on this thread so no need to post an event
                    if (data instanceof RefreshAccessTokenEvent) {
                        mCurrentJob = null;
                        return;
                    }

                    mBus.post(data);
                }
                mCurrentJob = null;
            }
        }

        public void runJob(Job job) {
            sendMessage(obtainMessage(MSG_DO_JOB, job));
        }

        private void handleRefreshAccessTokenEvent(RefreshAccessTokenEvent refreshAccessTokenEvent) {
            RefreshAccessToken.RefreshAccessTokenData data = refreshAccessTokenEvent.getEventData().getModelData();
            SettingsProvider.getInstance().saveAccessToken(data.getAccessToken());
            SettingsProvider.getInstance().saveExpiresIn(data.getExpiresIn());
            SettingsProvider.getInstance().saveRefreshToken(data.getRefreshToken());
            SettingsProvider.getInstance().saveScope(data.getScope());
            SettingsProvider.getInstance().saveTokenType(data.getTokenType());
        }


        private BaseEvent execute(Job job) {
            try {
                DataEvent data = executeJob(job);
                if (data == null) {
                    return new ErrorEvent(job.getTicket(), job.getRequest(), mContext.getString(R.string.GENERIC_ERROR), null);
                }

                // Handling refresh token event on the same thread that all request are made on. This way we no the refresh will be complete before making any other calls
                if (data instanceof RefreshAccessTokenEvent)
                    handleRefreshAccessTokenEvent((RefreshAccessTokenEvent) data);


                return data;
//                if (!data.isSuccess()) {
//                    return new ErrorEvent(job.getTicket(), job.getRequest(), data.getEventData().getMessage());
//                } else {
//                    return data;
//                }
            } catch (RetrofitError err) {
                int statusCode = (err.getResponse() != null) ? err.getResponse().getStatus() : UNRESOLVED_HOST;
                if (statusCode == UNAUTHORIZED) {
//                    BaseModel model = (BaseModel) err.getBodyAs(BaseModel.class);
                    return new AuthorizationErrorEvent(job.getTicket(), job.getRequest(), err.getMessage());
                }
                else if (statusCode == FORBIDDEN) {
                    return new ForbiddenErrorEvent(job.getTicket(), job.getRequest(), err.getMessage());
                }
                else if (statusCode == UNRESOLVED_HOST) {
                    if (!WebApiManager.isHaveActiveNetworkConnection(mContext)) {
                        return new UnrsolvedHostErrorEvent(job.getTicket(), job.getRequest(), mContext.getString(R.string.connection_error));
                    } else
                        return new UnrsolvedHostErrorEvent(job.getTicket(), job.getRequest(), err.getMessage());
                } else {
                    Log.d(TAG, "Request failed: " + job.getRequest(), err.getCause());
                    return new ErrorEvent(job.getTicket(), job.getRequest(), "(" + statusCode + ") " + mContext.getString(R.string.GENERIC_ERROR), err);
                }
            }
        }

        public void cancelAllJobs(boolean includeCurrent) {
            synchronized (this) {
                removeCallbacksAndMessages(null);
                if (includeCurrent && mCurrentJob != null) {
                    mCurrentJob.cancel();
                }
            }
        }
    }

    public static boolean isHaveActiveNetworkConnection(Context context) {

        NetworkInfo ni = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (ni == null)
            return false;
        return ni.isConnectedOrConnecting();
    }
}

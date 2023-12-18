package it.unimib.readify.source;

import java.util.List;

public interface BookCallback {
    void onSuccessFromRemote(NewsApiResponse newsApiResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<News> newsList);
    void onFailureFromLocal(Exception exception);
    void onNewsFavoriteStatusChanged(News news, List<News> favoriteNews);
    void onNewsFavoriteStatusChanged(List<News> news);
    void onDeleteFavoriteNewsSuccess(List<News> favoriteNews);

}

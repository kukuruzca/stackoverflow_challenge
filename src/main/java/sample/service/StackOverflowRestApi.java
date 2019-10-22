package sample.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sample.service.responses.CommonWrapperObject;
import sample.service.responses.Filter;
import sample.service.responses.FilterType;
import sample.service.responses.StackOverflowUser;

import java.util.List;

public interface StackOverflowRestApi {
    @GET("2.2/users")
    Call<CommonWrapperObject<StackOverflowUser>> users(
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("site") String site);

    @GET("2.2/users")
    Call<CommonWrapperObject<StackOverflowUser>> usersPaged(
            @Query("sort") String sort,
            @Query("min") int min,
            @Query("max") int max,
            @Query("order") String order,
            @Query("site") String site,
            @Query("pagesize") int pagesize,
            @Query("page") int page);

    @GET("2.2/users/{ids}")
    Call<CommonWrapperObject<StackOverflowUser>> usersById(
            @Path("ids")
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("site") String site);

    @GET("2.2/users/{ids}/tags")
    Call<CommonWrapperObject<StackOverflowUser>> usersTags(
            @Path("ids")
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("site") String site);

    @GET("2.2/filters/create")
    Call<CommonWrapperObject<Filter>> createFilter(
            @Query("include") List<String> include,
            @Query("exclude") List<String> exclude,
            @Query("base") String base,
            @Query("unsafe") boolean unsafe);

    @GET("2.2/filters/{filters}")
    Call<CommonWrapperObject<Filter>> getFilters(
            @Path("filters") String filters);
}


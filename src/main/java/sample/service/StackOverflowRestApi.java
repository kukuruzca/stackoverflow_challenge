package sample.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sample.service.stackoverflow.objects.CommonWrapperObject;
import sample.service.stackoverflow.objects.Filter;
import sample.service.stackoverflow.objects.User;
import sample.service.stackoverflow.objects.Tag;

import java.util.List;

public interface StackOverflowRestApi {
    @GET("2.2/users")
    Call<CommonWrapperObject<User>> users(
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("site") String site
    );

    @GET("2.2/users")
    Call<CommonWrapperObject<User>> usersPaged(
            @Query("sort") String sort,
            @Query("min") int min,
            @Query("max") int max,
            @Query("order") String order,
            @Query("site") String site,
            @Query("pagesize") int pagesize,
            @Query("page") int page
    );

    @GET("2.2/users")
    Call<CommonWrapperObject<User>> usersPagedWithFilter(
            @Query("sort") String sort,
            @Query("min") int min,
            @Query("max") int max,
            @Query("order") String order,
            @Query("site") String site,
            @Query("pagesize") int pagesize,
            @Query("page") int page,
            @Query("filter") String filter
    );

    @GET("2.2/users/{ids}")
    Call<CommonWrapperObject<User>> usersById(
            @Path("ids")
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("site") String site
    );

    @GET("2.2/users/{ids}/tags")
    Call<CommonWrapperObject<User>> usersTags(
            @Path("ids")
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("site") String site
    );

    @GET("2.2/filters/create")
    Call<CommonWrapperObject<Filter>> createFilter(
            @Query("include") List<String> include,
            @Query("exclude") List<String> exclude,
            @Query("base") String base,
            @Query("unsafe") boolean unsafe
    );

    @GET("2.2/filters/{filters}")
    Call<CommonWrapperObject<Filter>> getFilters(
            @Path("filters") String filters);

    @GET("2.2/users/{ids}/tags")
    Call<CommonWrapperObject<Tag>> getUserTags(
            @Path("ids") int ids,
            @Query("site") String site);
}


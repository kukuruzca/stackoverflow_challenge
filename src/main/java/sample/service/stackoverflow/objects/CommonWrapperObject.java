package sample.service.stackoverflow.objects;

import java.util.List;

public class CommonWrapperObject<T> {
    public final int quota_max;
    public final int quota_remaining;
    public final String error_message;
    public final String error_name;
    public final boolean has_more;
    public final List<T> items;

    public CommonWrapperObject(int quota_max, int quota_remaining, String error_message, String error_name, boolean has_more, List<T> items) {
        this.quota_max = quota_max;
        this.quota_remaining = quota_remaining;
        this.error_message = error_message;
        this.error_name = error_name;
        this.has_more = has_more;
        this.items = items;
    }
}

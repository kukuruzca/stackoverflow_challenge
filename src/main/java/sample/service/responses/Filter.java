package sample.service.responses;

public class Filter {
    public final String filter;
    public final FilterType filter_type;

    public Filter(String filter, FilterType filter_type) {
        this.filter = filter;
        this.filter_type = filter_type;
    }
}

package server;

import java.util.List;

public record ListResult(List<ListResult2> games) {
    public List<ListResult2> list(){
        return games;
    }
}

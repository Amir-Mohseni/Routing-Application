import dbTables.PostAddress;

import java.util.ArrayList;

public record QueryResponse(ArrayList<PostAddress> path, double distance, long time) {
}

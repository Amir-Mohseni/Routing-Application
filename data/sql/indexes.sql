CREATE INDEX idx_stops_stop_id ON stops (stop_id);

CREATE INDEX idx_routes_route_id ON routes (route_id);

CREATE INDEX idx_trips_trip_id ON trips (trip_id);
CREATE INDEX idx_trips_route_id ON trips (route_id);

CREATE INDEX idx_stop_times_trip_id ON stop_times (trip_id);
CREATE INDEX idx_stop_times_stop_id ON stop_times (stop_id);
CREATE INDEX idx_stop_times_departure_time ON stop_times (departure_time);
CREATE INDEX idx_stop_times_arrival_time ON stop_times (arrival_time);
CREATE INDEX idx_stop_times_stop_sequence ON stop_times (stop_sequence);

-- Composite Indexes
CREATE INDEX idx_stop_times_trip_stop_sequence ON stop_times (trip_id, stop_id, stop_sequence);

CREATE INDEX idx_stop_times_stop_departure_time ON stop_times (stop_id, departure_time);
CREATE INDEX idx_stop_times_stop_arrival_time ON stop_times (stop_id, arrival_time);

CREATE INDEX idx_stop_times_covering ON stop_times (trip_id, stop_id, departure_time, arrival_time, stop_sequence);

CREATE INDEX idx_trips_trip_route ON trips (trip_id, route_id);

CREATE INDEX idx_routes_route_names ON routes (route_id, route_short_name, route_long_name);

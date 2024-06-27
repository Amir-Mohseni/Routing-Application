import dbTables.AddressScore;
import dbTables.DirectRoute;
import dbTables.PostAddress;
import dbTables.Stop;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrawingUtils {
    //Bounds of map, used to draw on the map image
    public static final double minLat = 50.871838;
    public static final double maxLon = 5.745668;
    public static final double minLon = 5.638466;
    public static final double maxLat = 50.812057;


    public DrawingUtils() {

    }

    //Map Images, one clear and the other used to draw on top of
    private static final BufferedImage clearMapImage;
    private static final BufferedImage mapImage;

    static {
        try {
            clearMapImage = ImageIO.read(new File("data/img/Map.png"));
            mapImage = ImageIO.read(new File("data/img/Map.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getMapImage() {
        return mapImage;
    }

    void drawStraightLine(PostAddress startPoint, PostAddress endPoint) {
        Graphics2D g = getMapGraphics();

        Point fromPoint = findPostCodeCoordinate(startPoint.getLon(), startPoint.getLat());
        Point toPoint = findPostCodeCoordinate(endPoint.getLon(), endPoint.getLat());
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(4));
        g.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
        g.setColor(Color.GREEN);
        g.fillOval(fromPoint.x - 5, fromPoint.y - 5, 10, 10);
        g.setColor(Color.BLUE);
        g.fillOval(toPoint.x - 5, toPoint.y - 5, 10, 10);
    }


    //maps the coordinates onto the image by transforming them into relative pixel positions
    Point findPostCodeCoordinate(double lon, double lat) {
        int imageWidth = mapImage.getWidth();
        int imageHeight = mapImage.getHeight();
        double lonPercent = (lon - getMinLon()) / (getMaxLon() - getMinLon());
        double latPercent = (lat - getMinLat()) / (getMaxLat() - getMinLat());
        int xPixel = (int) (lonPercent * imageWidth);
        int yPixel = (int) (latPercent * imageHeight);


        return new Point(xPixel, yPixel);
    }

    //Visualization for Shortest Path Algorithm
    void drawShortestPathOnMap(ArrayList<PostAddress> shortestPath) {
        Graphics2D g = getMapGraphics();
        DrawBaseImage(g);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3));

        for (int i = 0; i < shortestPath.size() - 1; i++) {
            Point startPoint = findPostCodeCoordinate(shortestPath.get(i).getLon(), shortestPath.get(i).getLat());
            Point endPoint = findPostCodeCoordinate(shortestPath.get(i + 1).getLon(), shortestPath.get(i + 1).getLat());
            g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }
    }

    public static double getMinLat() {
        return minLat;
    }

    public static double getMaxLon() {
        return maxLon;
    }

    public static double getMinLon() {
        return minLon;
    }

    public static double getMaxLat() {
        return maxLat;
    }


    //draws the Bus route with transfer on the map
    public void drawShortestPathOnMapBusRouteTransfer(java.util.List<Stop> totalStops, List<Integer> transferIndices) {

        Graphics2D g = getMapGraphics();
        DrawBaseImage(g);
        g.setStroke(new BasicStroke(3));
        int circleSize = 6;
        int textPadding = 4;
        int offsetX = 20;
        Font font = new Font("Arial", Font.PLAIN, 12);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);

        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK};
        int colorIndex = 0;

        for (int i = 0; i < totalStops.size() - 1; i++) {
            Stop startStop = totalStops.get(i);
            Stop endStop = totalStops.get(i + 1);
            Point startPoint = findPostCodeCoordinate(startStop.stopLon(), startStop.stopLat());
            Point endPoint = findPostCodeCoordinate(endStop.stopLon(), endStop.stopLat());


            if (transferIndices.contains(i)) {
                colorIndex = (colorIndex + 1) % colors.length;
            }

            g.setColor(colors[colorIndex]);
            g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }


        for (int i = 0; i < totalStops.size(); i++) {
            Stop stop = totalStops.get(i);
            Point point = findPostCodeCoordinate(stop.stopLon(), stop.stopLat());

            if (transferIndices.contains(i)) {
                g.setColor(Color.RED);
                g.fillOval(point.x - 5, point.y - 5, 10, 10);
            } else {
                g.setColor(Color.BLUE);
                g.fillOval(point.x - 3, point.y - 3, 6, 6);
            }
        }


        for (int i = 0; i < totalStops.size(); i++) {
            Stop stop = totalStops.get(i);
            Point point = findPostCodeCoordinate(stop.stopLon(), stop.stopLat());
            String stopName = stop.stopName().replace("Maastricht, ", "");


            int textWidth = metrics.stringWidth(stopName);
            int textHeight = metrics.getHeight();


            int textX = point.x - textWidth / 2 + offsetX;
            int textY = point.y - circleSize / 2 - textPadding;

            if (i % 2 == 0 || i == totalStops.size() - 1) {

                g.setColor(Color.WHITE);
                g.fillRect(textX - textPadding, textY - textHeight + textPadding / 2, textWidth + 2 * textPadding, textHeight);


                g.setColor(Color.BLACK);
                g.drawRect(textX - textPadding, textY - textHeight + textPadding / 2, textWidth + 2 * textPadding, textHeight);


                if (i == 0 || i == totalStops.size() - 1 || transferIndices.contains(i)) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLACK);
                }


                g.drawString(stopName, textX, textY);
            }
        }
    }

    //Draws the Bus Route without transfers to the map Image
    void drawShortestPathOnMapBusRoute(DirectRoute route) {
        Graphics2D g = getMapGraphics();
        DrawBaseImage(g);

        g.setStroke(new BasicStroke(3));
        int circleSize = 6;
        int textPadding = 4;
        Font font = new Font("Arial", Font.PLAIN, 12);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);

        for (int i = 0; i < route.getBusStops().size() - 1; i++) {
            g.setColor(Color.RED);

            Point startPoint = findPostCodeCoordinate(route.getBusStops().get(i).getStopLon(), route.getBusStops().get(i).getStopLat());
            Point endPoint = findPostCodeCoordinate(route.getBusStops().get(i + 1).getStopLon(), route.getBusStops().get(i + 1).getStopLat());
            g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);

            g.setColor(Color.BLACK);
            g.fillOval(startPoint.x - circleSize / 2, startPoint.y - circleSize / 2, circleSize, circleSize);


            if (i % 2 == 0) {
                String stopName = route.getBusStops().get(i).getStopName().replaceFirst("^Maastricht, ", "");
                int textWidth = metrics.stringWidth(stopName);
                int textHeight = metrics.getHeight();
                int textX = startPoint.x - textWidth / 2 + 40;
                int textY = startPoint.y - circleSize / 2 - textPadding;


                g.setColor(Color.WHITE);
                g.fillRect(textX - textPadding, textY - textHeight + textPadding / 2, textWidth + 2 * textPadding, textHeight);


                g.setColor(Color.BLACK);
                g.drawRect(textX - textPadding, textY - textHeight + textPadding / 2, textWidth + 2 * textPadding, textHeight);
                g.setColor(Color.BLACK);

                if (i == 0) {
                    g.setColor(Color.RED);
                }

                g.drawString(stopName, textX, textY);
            }
        }


        Point lastPoint = findPostCodeCoordinate(route.getBusStops().get(route.getBusStops().size() - 1).getStopLon(), route.getBusStops().get(route.getBusStops().size() - 1).getStopLat());
        g.fillOval(lastPoint.x - circleSize / 2, lastPoint.y - circleSize / 2, circleSize, circleSize);
        String lastStopName = route.getBusStops().get(route.getBusStops().size() - 1).getStopName().replaceFirst("^Maastricht, ", "");
        int lastTextWidth = metrics.stringWidth(lastStopName);
        int lastTextHeight = metrics.getHeight();
        int lastTextX = lastPoint.x - lastTextWidth / 2 + 40;
        int lastTextY = lastPoint.y - circleSize / 2 - textPadding;


        g.setColor(Color.WHITE);
        g.fillRect(lastTextX - textPadding, lastTextY - lastTextHeight + textPadding / 2, lastTextWidth + 2 * textPadding, lastTextHeight);


        g.setColor(Color.BLACK);
        g.drawRect(lastTextX - textPadding, lastTextY - lastTextHeight + textPadding / 2, lastTextWidth + 2 * textPadding, lastTextHeight);


        g.setColor(Color.RED);
        g.drawString(lastStopName, lastTextX, lastTextY);
    }

    //find the proper color of the point using its score, blue is lowest score and red the highest score
    private static Color getColorForScore(double score) {
        int red = (int) (255 * (score / 100));
        int blue = 255 - red;
        return new Color(red, 0, blue);
    }


    //creates the heat Map when its button gets clicked, loops over all the AddressScore object in the scores list and handles them accordingly
    public void createHeatMap(List<AddressScore> scores) {
        Graphics2D g = getMapGraphics();
        DrawBaseImage(g);

        int counter = 0;
        for (AddressScore addressScore : scores) {

            //find coordinates for placement on the map image and the score to decide on the hue with later calculations
            double lon = addressScore.getAddress().getLon();
            double lat = addressScore.getAddress().getLat();
            double score = addressScore.getScore();

            //get the color of the point
            Color color = getColorForScore(score);
            Point point = findPostCodeCoordinate(lon, lat);

            if (point != null) {

                g.setColor(color);
                g.fillOval(point.x - 5, point.y - 5, 10, 10);
            } else {
                System.err.println("Invalid coordinates for lon: " + lon + ", lat: " + lat);
            }
        }
    }


    //gets the Map Graphics from the Map Image
    public Graphics2D getMapGraphics() {
        return (Graphics2D) mapImage.getGraphics();
    }

    //Clears previous drawings on the map Image
    public void DrawBaseImage(Graphics2D g) {
        g.drawImage(clearMapImage, 0, 0, null);
    }
}

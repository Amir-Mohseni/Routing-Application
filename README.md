# **Project 1-2**
# Transportation Application for Maastricht

## Overview
The Maastricht Transportation Application is a project that aims to provide users with information on transportation options between two locations within Maastricht, Netherlands. The application allows users to input two postal codes and choose their preferred mode of transportation: Walk, Bike, Car, or Bus. It then calculates and displays the route, estimated time, and distance for the selected transportation mode.

## Features
- **Mode Selection**: Users can choose between Walk, Bike, Car, or Bus as their mode of transportation.
- **Routing**: The application calculates the route between two locations using either straight-line distance or the shortest distance based on a pathfinding algorithm.
- **Bus Information**: For Bus mode, the application retrieves data from a GTFS file containing information about bus routes and schedules. It then queries a server to find the shortest path and provides information on the bus route, stops, and estimated time.
- **Visualization**: The application visualizes the selected route on a map of Maastricht, allowing users to see their journey visually.
- **GUI**: The application is designed with a Graphical User Interface (GUI) for ease of use.
- **Time Selection**: Users can pick the time for their bus travels, choosing when they plan to leave.
- **Transfer Options**: Users can choose if they want transfers in bus routes, giving them more control over their travel plans.
- **Accessibility Score**: Displays a score for each neighborhood from 0 to 100 based on proximity to essential and non-essential amenities.

## Accessibility Score Calculation
The accessibility score for each neighborhood is based on proximity to various amenities. The amenities are categorized and weighted as follows:
### Category Weights:
- **Shop Weight:** 0.3
- **Amenity Weight:** 0.4
- **Tourism Weight:** 0.1
- **Accessibility Weight:** 0.2

The calculated distance score is then multiplied by the weight assigned to each amenity category to derive the final accessibility score.

## Installation
To install and run the application, follow these steps:

1. Clone the repository: `git clone https://github.com/Amir-Mohseni/Routing-Application.git`
2. Navigate to the project directory: `cd Routing-Application`
3. Run the Maven installation script based on your operating system:
   - For Unix/Linux: `./maven_script.sh`
   - For Windows: `maven_script.bat`
4. If you want to populate your own database, use the `Maastricht Data` folder which contains the necessary data for Maastricht using CSV files.

## Environment Variables

The following environment variables can be set for the application. If not set, default values will be used:

- `DB_HOST` (default: `localhost`)
- `DB_PORT` (default: `3306`)
- `DB_USERNAME` (default: `root`)
- `DB_PASSWORD` (default: `""` - empty string)
- `DB_NAME` (default: `defaultdb`)


## Usage
1. Open the project in your preferred Java IDE.
2. Run the `GUI.java` file to launch the application.
3. Enter two postal codes for the starting and destination locations.
4. Choose your preferred mode of transportation.
5. Click on "Calculate Route" to view the visualized route, and estimated time.

## Research Paper
This project is accompanied by a research paper that provides detailed information on the methodologies, algorithms, and data sources used in the development of this application. The research paper can be accessed [here](https://docs.google.com/document/d/1l6J6unJrMj5cpVTA6_UVfETZJUGGveTdMWLvvOa873c/edit?usp=sharing).

## Examples
Here are some examples of how to use the application:

**Example 1:**
- Press the "Accessibility Score" button
- Select postal code: 6216EG
- View the accessibility score for the neighborhood

![Example 1](data/img/screenshots/example_1.png)

**Example 2:**
- Choose Bike mode
- Press the "Shortest Route" button
- Select postal codes: 6216EG and 6221CR

![Example 2](data/img/screenshots/example_2.png)

**Example 3:**
- Select departure time: 08:00 AM
- Choose Bus mode
- Choose no transfers
- Select postal codes: 6221CR and 6229EN

![Example 3](data/img/screenshots/example_3.png)

**Example 4:**
- Select departure time: 03:00 PM
- Choose Bus mode
- Choose transfers
- Select postal codes: 6218BK and 6229GV

![Example 4](data/img/screenshots/example_4.png)
![Example 4 Details](data/img/screenshots/example_4_details.png)

## Images
### Image 1: Route Visualization
![Route Visualization](data/img/screenshots/example_4.png)

### Image 2: Accessibility Score Heatmap
![Accessibility Score Heatmap](data/img/screenshots/accessibility_score_heatmap.png)

## Dependencies
- Java 21
- Maven
- Python (for preprocessing GTFS file)
- Pandas (Python library)
- MySQL (for database)

## License
This project is licensed under the MIT License.
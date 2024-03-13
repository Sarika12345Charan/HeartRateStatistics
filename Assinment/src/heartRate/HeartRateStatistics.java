package heartRate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HeartRateStatistics {

    public static void main(String[] args) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
          
			//objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            JsonNode rootNode = objectMapper.readTree(new File("heartrate.json"));

            List<StatisticsEntry> statisticsList = new ArrayList<>();

            Iterator<JsonNode> iterator = rootNode.iterator();
            while (iterator.hasNext()) {
                JsonNode measurementNode = iterator.next();

                // Extracting relevant data
                JsonNode beatsPerMinuteNode = measurementNode.get("beatsPerMinute");
                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                int totalBeats = 0;
                int count = 0;
                String latestDataTimestamp = null;

                for (JsonNode beatsNode : beatsPerMinuteNode) {
                    int beatsPerMinute = beatsNode.asInt();
                    totalBeats += beatsPerMinute;
                    min = Math.min(min, beatsPerMinute);
                    max = Math.max(max, beatsPerMinute);
                    count++;
                }

                if (count > 0) {
                    int median = totalBeats / count;

                    JsonNode timestampsNode = measurementNode.get("timestamps");
                    String startTime = timestampsNode.get("startTime").asText();
                    String endTime = timestampsNode.get("endTime").asText();
                    latestDataTimestamp = endTime;

                    // Extracting date from the latestDataTimestamp
                    String date = latestDataTimestamp.substring(0, 10);

                    // Creating a StatisticsEntry object
                   StatisticsEntry entry = new StatisticsEntry(date, min, max, median, latestDataTimestamp);
                    statisticsList.add(entry);
                }
            }


            // Displaying the result
            for (StatisticsEntry entry : statisticsList) {
                System.out.println(entry);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class StatisticsEntry {
        String date;
        int min;
        int max;
        int median;
        String latestDataTimestamp;

        public StatisticsEntry(String date, int min, int max, int median, String latestDataTimestamp) {
            this.date = date;
            this.min = min;
            this.max = max;
            this.median = median;
            this.latestDataTimestamp = latestDataTimestamp;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"date\": \"" + date + "\"" +
                    ", \"min\": " + min +
                    ", \"max\": " + max +
                    ", \"median\": " + median +
                    ", \"latestDataTimestamp\": \"" + latestDataTimestamp + "\"" +
                    "}";
        }
    }
}
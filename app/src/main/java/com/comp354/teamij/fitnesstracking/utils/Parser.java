package com.comp354.teamij.fitnesstracking.utils;

import com.comp354.teamij.fitnesstracking.entities.WeatherResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

public class Parser {

    private static CsvReader csvReader = new CsvReader();

    private static final String START_OF_CONTENT = "Date/Time";

    /**
     * Convert a string weather response into a list of WeatherResponse
     * @param input String response
     * @return List of WeatherResponses
     * @throws IOException
     */
    public static List<WeatherResponse> stringToItems(String input) throws IOException {
        LinkedList<WeatherResponse> items = new LinkedList<>();
        boolean desiredContent = false;
        BufferedReader reader = new BufferedReader(new StringReader(input));
        StringBuilder cleanedInput = new StringBuilder();
        String line, firstField;
        while ((line = reader.readLine()) != null) {
            String[] elements = line.split(",");
            if (elements.length >= 1) {
                if (desiredContent) {
                    cleanedInput.append(line);
                    cleanedInput.append("\n");
                    continue;
                }
                firstField = elements[0].trim();
                firstField = firstField.replaceAll("\"", "");
                if (firstField.compareToIgnoreCase(START_OF_CONTENT) == 0) {
                    desiredContent = true;
                    cleanedInput.append(line);
                    cleanedInput.append("\n");
                }
            }
        }

        input = cleanedInput.toString();
        csvReader.setContainsHeader(true);
        csvReader.setFieldSeparator(',');
        csvReader.setTextDelimiter('\"');
        CsvParser csvParser = csvReader.parse(new StringReader(input));
        CsvRow row;
        while ((row = csvParser.nextRow()) != null) {
            Map<String, String> values = row.getFieldMap();
            String temp = values.get("Mean Temp (Â°C)");

            // if wind speed is under 31 or missing, assume no wind that day
            String windSpeed = values.get("Spd of Max Gust (km/h)");
            if (windSpeed.equals("<31") || windSpeed.equals("")) {
                windSpeed = "0";
            }

            // if temperature is empty, weather data is not available for that day
            if (!temp.equals("")) {
                WeatherResponse response = new WeatherResponse();
                response.setDateTime(values.get("Date/Time"));
                response.setTemperature(temp);
                response.setWindSpeed(windSpeed);

                items.add(response);
            }
        }
        return items;
    }
}

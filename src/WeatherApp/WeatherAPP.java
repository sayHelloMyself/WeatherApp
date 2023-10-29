import javax.swing.*;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;

public class WeatherAPP {
    public static void main(String[] args) {
        JFrame frame = new JFrame("天气预报系统");
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("输入城市：");
        JTextField textFieldCity = new JTextField(20);
        JButton btnGetWeather = new JButton("获取天气");
        JTextArea textAreaWeatherInfo = new JTextArea(10, 40);
        textAreaWeatherInfo.setWrapStyleWord(true);
        textAreaWeatherInfo.setLineWrap(true);
        textAreaWeatherInfo.setEditable(false);

        btnGetWeather.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = textFieldCity.getText();
                if (!city.isEmpty()) {
                    try {
                        String weatherInfo = getWeatherInfo(city);
                        textAreaWeatherInfo.setText(weatherInfo);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        textAreaWeatherInfo.setText("无法获取天气信息。");
                    }
                }
            }
        });

        frame.add(label);
        frame.add(textFieldCity);
        frame.add(btnGetWeather);
        frame.add(new JScrollPane(textAreaWeatherInfo));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setVisible(true);
    }

    private static String getWeatherInfo(String city) throws Exception {
        String apiUrl = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx/getWeatherbyCityName?theCityName=" + city;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        connection.disconnect();

        // 解析XML并提取文本信息
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(response.toString())));
        NodeList nodeList = document.getElementsByTagName("string");

        StringBuilder weatherInfo = new StringBuilder();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String content = node.getTextContent();
            weatherInfo.append(content).append("\n");
        }

        return weatherInfo.toString();
    }
}

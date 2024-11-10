package io.littlehorse.simulations.stateful.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.littlehorse.simulations.stateful.app.dto.Cluster;
import io.littlehorse.simulations.stateful.app.dto.Node;
import io.littlehorse.simulations.stateful.app.dto.NodeStatus;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClusterScraping {

    private final List<URI> hosts;
    private static final int HTTP_PORT = 8088;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder().build();

    private final Template freemarkerTemplate;


    {

        Configuration cfg = new Configuration();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            URL resource = classloader.getResource("templates");
            cfg.setTemplateLoader(new FileTemplateLoader(Paths.get(resource.toURI()).toFile()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        try {
            freemarkerTemplate = cfg.getTemplate("tasks.ftl.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ClusterScraping(final String host1, final String host2, final String host3){
        this.hosts = List.of(
            URI.create(host1 + "/streams/node"),
            URI.create(host2 + "/streams/node"),
            URI.create(host3 + "/streams/node")
        );
    }

    public static void main(String[] args){
        if(args.length != 3){
            throw new IllegalArgumentException("Must provide at most three app host");
        }
        String host1 = args[0];
        String host2 = args[1];
        String host3 = args[2];
        var clusterScrapping = new ClusterScraping(host1, host2, host3);
        Javalin httpServer = Javalin.create().start(HTTP_PORT);
        httpServer.get("streams/cluster", clusterScrapping::clusterInfo);
        httpServer.get("", clusterScrapping::renderClusterInfo);
    }

    public void clusterInfo(final Context context) {

        context.contentType(ContentType.APPLICATION_JSON);
        try {
            context.result(objectMapper.writeValueAsString(getClusterInfo()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void renderClusterInfo(final Context context) {
        context.contentType(ContentType.TEXT_HTML);
        StringWriter stringWriter = new StringWriter();
        try {
            freemarkerTemplate.process(Map.of("cluster", getClusterInfo()), stringWriter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        context.result(stringWriter.toString());
    }

    private Cluster getClusterInfo(){
        List<Node> nodes = hosts.stream().map(this::hostInfo).toList();
        var clusterState = nodes.stream()
                .filter(node -> node.getStatus() == NodeStatus.RUNNING)
                .map(Node::getClusterState).findFirst()
                .orElse("DOWN");
        return new Cluster(clusterState, nodes);
    }

    private Node hostInfo(final URI host){
        try {
            var request = HttpRequest.newBuilder(host).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return jsonToNode(response.body());
        } catch (Exception e) {
            return Node.unreachableHost();
        }
    }

    private Node jsonToNode(String json){
        try {
            return objectMapper.readValue(json, Node.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

window.onload = function() {
  //<editor-fold desc="Changeable Configuration Block">

  // base_host_url = "https://streaming.tuneurl-demo.com";
  const base_host_url = "https://ec2-18-117-196-219.us-east-2.compute.amazonaws.com";

  // the following lines will be replaced by docker/configurator, when it runs in a docker-container
  window.ui = SwaggerUIBundle({
    url: base_host_url + "/v3/v3tuneurlpoc.json",
    dom_id: '#swagger-ui',
    deepLinking: true,
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    plugins: [
      SwaggerUIBundle.plugins.DownloadUrl
    ],
    layout: "StandaloneLayout"
  });

  //</editor-fold>
};

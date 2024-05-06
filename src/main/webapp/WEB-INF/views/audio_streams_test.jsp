<!DOCTYPE html>
<html lang="en">
<!-- <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<!-- -->
<!-- ## JavaScript TuneURL SDK version 1.0.4 -->
<!--  -->
<!-- ### Version 1.0.5 -->
<!--  -->
<!-- | Item       | Details                 | Changes | -->
<!-- | ---------- | ----------------------- | ------- | -->
<!-- |       Date | October 23, 2023        | N/A | -->
<!-- | Release by | Teodoro Albon           | N/A | -->
<!-- |    Version | 1.0.1 Initial Release   | v1.0.1 -->
<!-- |    Version | 1.0.2 November 23, 2023 | v1.0.2 -->
<!-- |    Version | 1.0.3 December 5, 2023  | using js-sdk-1.0.3.min.js | -->
<!-- |    Version | 1.0.4 December 11, 2023 | using js-sdk-1.0.4.min.js | -->
<!-- |    Version | Sat Jan 6 06:59:06 2024 +0800 | using js-audio-streams-test.js | -->
<!-- |    Version | Wed Mar 13 16:23:06 2024 +0800 | using js-sdk-audio-data-1.0.5.js | -->
<!-- -->

<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Stream Radio POC</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="Cache-Control" content="no-cache">
    <link rel="stylesheet" type="text/css" href="/bootstrap/css/bootstrap.min.css" />
    <link rel="stylesheet" type="text/css" href="/css/tuneurl-style.css" />
    <script type="text/javascript" src="/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="/js/jqbs64.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.bundle.min.js"></script>
</head>

<body id="body" onload="javascript:startCanvas(this);">
    <div id="main-container" class="container-fluid p-5">
        <div id="hiddendata" style="display:none" ><input type="hidden" id="usertoken" name="usertoken" value="${token}"/></div>
        <div class="modal fade" id="thisModal" tabindex="-1" role="dialog" aria-labelledby="thisModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content d-flex justify-content-center primeColor2">
                    <div id="modal-question" class="d-flex justify-content-center primeColor3text fs-2 mt-3 mb-3">Are you Interested?</div>
                    <div class="text-center mb-4">
                        <button onclick="javascript:executeChannelModal(1);" type="button" class="btn btn-danger btn-lg btn-spacer-y">YES</button>
                        <button onclick="javascript:executeChannelModal(0);" type="button" class="btn btn-secondary btn-lg">NO</button>
                    </div>
                </div>
            </div>
        </div><!--#thisModal-->
        <div id="canvas-border" class="container-sm p-3" style="display:block">
            <h4 id="title">Stream Radio POC</h4>
            <div class="container-sm red p-3 primeColor1">
                <div class="container-sm red pt-5 px-5 pb-3 mt-3 primeColor2">
                    <div id="pocTitle" class="d-flex justify-content-center primeColor3text fs-4">Audio Demo Test</div>
                </div>
                <div class="container-sm red p-5 mt-3 primeColor2">
                    <div id="instruction" class="d-flex justify-content-center primeColorWhite">
                        <audio id="audioId"></audio>
                    </div>
                    <div id="channelbtn"></div>
                    <div id="playstop">
                        <center><button id="play" style="display:none" data-playing="false" role="switch" aria-checked="false" onclick="javascript:playonclick();">
                          <span id="playorpause">Play</span>
                        </button></center>

                    </div>
                </div>
            </div>
        </div><!--#canvas-border-->
        <div id="spinner" class="spinner-border text-danger" role="status" style="display:none">
            <span class="sr-only">Loading...</span>
        </div><!--#spinner-->
    </div><!--#main-container-->
</body>
<script type="text/javascript" src="/js/audio-demo.js"></script>

</html>
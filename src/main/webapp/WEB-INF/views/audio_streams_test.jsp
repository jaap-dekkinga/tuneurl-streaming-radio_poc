<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="UTF-8">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>Stream Radio POC</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="Cache-Control" content="no-cache">
	<!-- Latest compiled and minified CSS -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" rel="stylesheet">
	<!-- Latest compiled JavaScript -->
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<style>
		html,
		body {
			height: 100%;
			margin: 0px;
		}

		body {
			display: flex;
			justify-content: center;
			color: #212529;
			background-color: #fff;
			/* box-shadow: inset 0 0 2rem rgba(0, 0, 0, .5); */
		}

		.cover-container {
			max-width: 42em;
		}

		.masthead {
			margin-bottom: 2rem;
		}

		.masthead-brand {
			margin-bottom: 0;
		}

		.nav-masthead .nav-link {
			padding: .25rem 0;
			color: #212529;
			background-color: transparent;
			border-bottom: .25rem solid transparent;
		}

		.nav-masthead .nav-link:hover,
		.nav-masthead .nav-link:focus {
			border-bottom-color: rgba(0, 0, 0, .25);
		}

		.nav-masthead .nav-link+.nav-link {
			margin-left: 1rem;
		}

		@media (min-width: 48em) {
			.masthead-brand {
				float: left;
			}

			.nav-masthead {
				float: right;
			}
		}

		.cover {
			padding: 0 1.5rem;
		}

		#background {
			position: fixed;
			z-index: -1;
		}

		.header-image {
			height: 100%;
			padding: 10px;

		}

		header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			height: 60px;
			padding: 0;
		}

		#page {
			width: 100%;
		}

		.img-wrap {
			position: relative;
			margin: 20px auto;
			width: 270px;
			height: 200px;
			overflow: hidden;
			border-radius: 20px;
			box-shadow: 0px 10px 40px 0px rgba(39, 70, 132, 0.7);

			img {
				width: auto;
				height: 100%;
			}
		}

		#pocTitle {
			font-size: 50px;
			font-weight: 350;
		}

		button {
			border-radius: 50%;
			margin: 15px;
			font-size: 18px;
			text-align: center;
			transition: 0.2s;
			cursor: pointer;
			border: none;
			background: 0;
		}

		.play {
			width: 50px;
			height: 50px;
			border: 1px solid #e2e2e2;
		}

		.play:hover {
			left: 0;
			box-shadow: 0px 0px 15px 0px rgba(39, 70, 132, 0.7);
		}

		.fa-play {
			transform: translateX(2px);
		}
	</style>
</head>

<body id="body" onload="javascript:startCanvas(this);javascript:getStreamURL();">
	<!-- The Modal -->
	<div class="modal fade" id="thisModal">
		<div class="modal-dialog">
			<div class="modal-content">

				<!-- Modal Header -->
				<div class="modal-header">
					<h4 class="modal-title">Intraction</h4>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>

				<!-- Modal body -->
				<div class="modal-body">
					Are you interested?
				</div>

				<!-- Modal footer -->
				<div class="modal-footer">
					<button onclick="javascript:executeChannelModal(1);" type="button" class="btn btn-primary"
						data-bs-dismiss="modal">Yes</button>
					<button onclick="javascript:executeChannelModal(0);" type="button" class="btn btn-danger"
						data-bs-dismiss="modal">No</button>
				</div>

			</div>
		</div>
	</div>

	<div id="page" class="d-flex h-100 p-3 mx-auto flex-column">
		<header class="masthead">
			<img src="assets/logo.png" id="logoImage" alt="Left Image" class="header-image">
			<div>
				<button style="display: block; padding: 0px;" class="btn dropdown-toggle" type="button" id="dropdownMenuButton"
					data-bs-toggle="dropdown" aria-expanded="false"> <img src="assets/logo2.png" alt="Right Image"
						class="header-image">
				</button>
				<ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
					<li><a class="dropdown-item" href="#" data-src="assets/logo.png"
							data-value="https://stream.radiojar.com/vzv0nkgsw7uvv">Radiojar</a></li>
					<li><a class="dropdown-item" href="#" data-src="assets/libretime-logo.png"
							data-value="http://libretime.tuneurl-demo.com:8000/main">Libretime</a></li>
				</ul>
				<div class="" style="margin-left: 10px;font-weight: bold;">
					<span id="selectedItem">None</span>
				  </div>
			</div>
		</header>

		<div>

			<main role="main" class="inner cover">
				<div class="img-wrap">
					<img src="assets/background.jpg" />
				</div>

				<form style="width: 80%; margin: 0 auto; display: none;">
					<div class="form-row">
						<div class="form-group col-sm-6">
							<label for="noiseInput" class="form-label">Noise Coef</label>
							<input type="range" min="1" max="100" class="custom-range" id="noiseInput">
						</div>
						<div class="form-group col-sm-6">
							<label for="heightInput" class="form-label">Height Coef</label>
							<input type="range" min="1" max="100" class="custom-range" id="heightInput">
						</div>
					</div>
					<div class="form-group col-md-12">
						<a href="#" id="trigger" class="btn btn-sm btn-secondary">Random Colors</a>
					</div>
				</form>

				<div id="hiddendata" style="display:none"><input type="hidden" id="usertoken" name="usertoken"
						value="${token}" /></div>
				<div id="channelbtn"></div>
				<h1 id="pocTitle" class="d-flex justify-content-center">00:00</h1>

				<div class="d-flex justify-content-center">
					<div id="spinner" class="spinner-grow text-primary" role="status" style="display: none;"></div>
				</div>
				<div class="d-flex justify-content-center">
					<button id="play" style="display:none" class="play current-btn" onclick="togglePlayPause();"><i
							id="playicon" class="fas fa-play"></i></button>
				</div>
			</main>

			<footer class="mastfoot mt-auto">
			</footer>
		</div>
	</div>

	<canvas id="background"></canvas>

</body>
<script type="text/javascript" src="/js/audio-demo.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/simplex-noise/2.4.0/simplex-noise.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/chroma-js/2.1.0/chroma.min.js"></script>

<script>
	App({ el: 'background' });

	function togglePlayPause() {
		playonclick();
		const icon = document.getElementById('playicon');
		if (icon.classList.contains('fa-play')) {
			icon.classList.remove('fa-play');
			icon.classList.add('fa-pause');
		} else {
			icon.classList.remove('fa-pause');
			icon.classList.add('fa-play');
		}
	}

	function App(conf) {
		conf = {
			fov: 75,
			cameraZ: 75,
			xyCoef: 50,
			zCoef: 10,
			lightIntensity: 0.9,
			ambientColor: 0x000000,
			light1Color: 0x0E09DC,
			light2Color: 0x1CD1E1,
			light3Color: 0x18C02C,
			light4Color: 0xee3bcf,
			...conf
		};

		let renderer, scene, camera, cameraCtrl;
		let width, height, cx, cy, wWidth, wHeight;
		const TMath = THREE.Math;

		let plane;
		const simplex = new SimplexNoise();

		const mouse = new THREE.Vector2();
		const mousePlane = new THREE.Plane(new THREE.Vector3(0, 0, 1), 0);
		const mousePosition = new THREE.Vector3();
		const raycaster = new THREE.Raycaster();

		const noiseInput = document.getElementById('noiseInput');
		const heightInput = document.getElementById('heightInput');

		init();

		function init() {
			renderer = new THREE.WebGLRenderer({ canvas: document.getElementById(conf.el), antialias: true, alpha: true });
			camera = new THREE.PerspectiveCamera(conf.fov);
			camera.position.z = conf.cameraZ;

			updateSize();
			window.addEventListener('resize', updateSize, false);

			initScene();
			initGui();
			animate();
		}


		function initGui() {
			noiseInput.value = 101 - conf.xyCoef;
			heightInput.value = conf.zCoef * 100 / 25;

			noiseInput.addEventListener('input', e => {
				conf.xyCoef = 101 - noiseInput.value;
			});
			heightInput.addEventListener('input', e => {
				conf.zCoef = heightInput.value * 25 / 100;
			});

			document.getElementById('trigger').addEventListener('click', e => {
				updateLightsColors();
			});
		}

		function initScene() {
			scene = new THREE.Scene();
			initLights();

			let mat = new THREE.MeshLambertMaterial({ color: 0xffffff, side: THREE.DoubleSide });
			// let mat = new THREE.MeshPhongMaterial({ color: 0xffffff });
			// let mat = new THREE.MeshStandardMaterial({ color: 0x808080, roughness: 0.5, metalness: 0.8 });
			let geo = new THREE.PlaneBufferGeometry(wWidth, wHeight, wWidth / 2, wHeight / 2);
			plane = new THREE.Mesh(geo, mat);
			scene.add(plane);

			plane.rotation.x = -Math.PI / 2 - 0.2;
			plane.position.y = -25;
			camera.position.z = 60;
		}

		function initLights() {
			const r = 30;
			const y = 10;
			const lightDistance = 500;

			// light = new THREE.AmbientLight(conf.ambientColor);
			// scene.add(light);

			light1 = new THREE.PointLight(conf.light1Color, conf.lightIntensity, lightDistance);
			light1.position.set(0, y, r);
			scene.add(light1);
			light2 = new THREE.PointLight(conf.light2Color, conf.lightIntensity, lightDistance);
			light2.position.set(0, -y, -r);
			scene.add(light2);
			light3 = new THREE.PointLight(conf.light3Color, conf.lightIntensity, lightDistance);
			light3.position.set(r, y, 0);
			scene.add(light3);
			light4 = new THREE.PointLight(conf.light4Color, conf.lightIntensity, lightDistance);
			light4.position.set(-r, y, 0);
			scene.add(light4);
		}

		function animate() {
			requestAnimationFrame(animate);

			animatePlane();
			animateLights();

			renderer.render(scene, camera);
		};

		function animatePlane() {
			gArray = plane.geometry.attributes.position.array;
			const time = Date.now() * 0.0002;
			for (let i = 0; i < gArray.length; i += 3) {
				gArray[i + 2] = simplex.noise4D(gArray[i] / conf.xyCoef, gArray[i + 1] / conf.xyCoef, time, mouse.x + mouse.y) * conf.zCoef;
			}
			plane.geometry.attributes.position.needsUpdate = true;
			// plane.geometry.computeBoundingSphere();
		}

		function animateLights() {
			const time = Date.now() * 0.001;
			const d = 50;
			light1.position.x = Math.sin(time * 0.1) * d;
			light1.position.z = Math.cos(time * 0.2) * d;
			light2.position.x = Math.cos(time * 0.3) * d;
			light2.position.z = Math.sin(time * 0.4) * d;
			light3.position.x = Math.sin(time * 0.5) * d;
			light3.position.z = Math.sin(time * 0.6) * d;
			light4.position.x = Math.sin(time * 0.7) * d;
			light4.position.z = Math.cos(time * 0.8) * d;
		}

		function updateLightsColors() {
			conf.light1Color = chroma.random().hex();
			conf.light2Color = chroma.random().hex();
			conf.light3Color = chroma.random().hex();
			conf.light4Color = chroma.random().hex();
			light1.color = new THREE.Color(conf.light1Color);
			light2.color = new THREE.Color(conf.light2Color);
			light3.color = new THREE.Color(conf.light3Color);
			light4.color = new THREE.Color(conf.light4Color);
			// console.log(conf);
		}

		function updateSize() {
			width = window.innerWidth; cx = width / 2;
			height = window.innerHeight; cy = height / 2;
			if (renderer && camera) {
				renderer.setSize(width, height);
				camera.aspect = width / height;
				camera.updateProjectionMatrix();
				const wsize = getRendererSize();
				wWidth = wsize[0];
				wHeight = wsize[1];
			}
		}

		function getRendererSize() {
			const cam = new THREE.PerspectiveCamera(camera.fov, camera.aspect);
			const vFOV = cam.fov * Math.PI / 180;
			const height = 2 * Math.tan(vFOV / 2) * Math.abs(conf.cameraZ);
			const width = height * cam.aspect;
			return [width, height];
		}
	}

</script>

</html>
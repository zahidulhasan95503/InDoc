console.log("this is mapsscript")

let map, infoWindow;

async function initmap() {



	const { Map } = await google.maps.importLibrary("maps");

	const { AdvancedMarkerElement, PinElement } = await google.maps.importLibrary("marker");


	console.log(document.getElementById("map"))
	map = new Map(document.getElementById("map"), {

		zoom: 4,
		center: { lat: 17.7044941, lng: 83.1756083 },
		mapId: "5b6de97984a13ca6",

	});

	infoWindow = new google.maps.InfoWindow();

	const locationButton = document.getElementById("mylocation");


	console.log(locationButton.textContent)

	locationButton.classList.add("custom-map-control-button");
	//map.controls[google.maps.ControlPosition.TOP_CENTER].push(locationButton);
	locationButton.addEventListener("click", () => {
		// Try HTML5 geolocation.
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(
				(position) => {
					const pos = {
						lat: position.coords.latitude,
						lng: position.coords.longitude,
					};

					const icon = document.createElement("div");

					icon.innerHTML = '<i class="fa-solid fa-location-dot"></i>';

					const faPin = new PinElement({
						glyph: icon,
						glyphColor: "#ff8300",
						background: "#0000FF",
						borderColor: "#ff8300",
					});
					const faMarker = new AdvancedMarkerElement({
						map,
						position: pos,
						content: faPin.element,
						title: "A marker using a FontAwesome icon for the glyph.",
					});


					console.log(pos)
					map.setCenter(pos);
					map.setZoom(14);
					findPlaces(pos)
				},
				// () => {
				// 	handleLocationError(true, infoWindow, map.getCenter());
				// },


			);
		} else {
			// Browser doesn't support Geolocation
			handleLocationError(false, infoWindow, map.getCenter());
		}


	});


}

function handleLocationError(browserHasGeolocation, infoWindow, pos) {
	infoWindow.setPosition(pos);
	infoWindow.setContent(
		browserHasGeolocation
			? "Error: The Geolocation service failed."
			: "Error: Your browser doesn't support geolocation.",
	);
	infoWindow.open(map);
}

async function findPlaces(pos) {

	const legalplaces = document.getElementById("places");
	const { Place } = await google.maps.importLibrary("places");
	const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");

	// Show the cards section with loading spinner
	const section = document.getElementById("place-cards-section");
	const grid = document.getElementById("place-cards-grid");
	const loading = document.getElementById("cards-loading");
	const empty = document.getElementById("cards-empty");
	section.style.display = "block";
	loading.style.display = "block";
	grid.innerHTML = "";
	empty.style.display = "none";

	const request = {
		textQuery: legalplaces.textContent,
		fields: ["displayName", "location", "businessStatus", "formattedAddress"],
		locationBias: pos,
		language: "en-US",
	};

	const { places } = await Place.searchByText(request);

	if (!places || !places.length) {
		loading.style.display = "none";
		empty.style.display = "block";
		console.log("No results");
		return;
	}

	console.log(places);

	const { LatLngBounds } = await google.maps.importLibrary("core");
	const bounds = new LatLngBounds();

	// Add markers
	places.forEach((place) => {
		const markerView = new AdvancedMarkerElement({
			map,
			position: place.location,
			title: place.displayName,
			gmpClickable: true,
		});
		bounds.extend(place.location);
	});
	map.fitBounds(bounds);

	// ── Calculate distances using Haversine (no extra API call needed) ──
	function haversineDistance(p1, p2) {
		const R = 6371; // km
		const dLat = (p2.lat - p1.lat) * Math.PI / 180;
		const dLng = (p2.lng - p1.lng) * Math.PI / 180;
		const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
			Math.cos(p1.lat * Math.PI / 180) * Math.cos(p2.lat * Math.PI / 180) *
			Math.sin(dLng / 2) * Math.sin(dLng / 2);
		return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}

	// Build place data with distances
	const placeData = places.map((place) => {
		const placePos = { lat: place.location.lat(), lng: place.location.lng() };
		const distKm = haversineDistance(pos, placePos);
		// Rough driving estimate: avg 30 km/h in city
		const estMinutes = Math.round((distKm / 30) * 60);
		return {
			name: place.displayName || "Aadhaar Centre",
			address: place.formattedAddress || "Address not available",
			status: place.businessStatus || "UNKNOWN",
			distKm: distKm,
			distText: distKm < 1 ? (distKm * 1000).toFixed(0) + " m" : distKm.toFixed(1) + " km",
			timeText: estMinutes < 1 ? "< 1 min" : estMinutes + " min (approx)",
			lat: placePos.lat,
			lng: placePos.lng,
		};
	});

	// Sort closest first
	placeData.sort((a, b) => a.distKm - b.distKm);

	// ── Render cards ──
	loading.style.display = "none";

	placeData.forEach((p, i) => {
		const card = document.createElement("div");
		card.style.cssText = `
			background: #ffffff;
			border: 1px solid #e2e8f0;
			border-left: 5px solid #C5A059;
			border-radius: 12px;
			padding: 1.2rem 1.4rem;
			box-shadow: 0 4px 16px rgba(0,0,0,0.06);
			transition: transform 0.25s ease, box-shadow 0.25s ease;
			cursor: pointer;
		`;
		card.onmouseover = function () {
			this.style.transform = "translateY(-4px)";
			this.style.boxShadow = "0 10px 28px rgba(0,0,0,0.12)";
		};
		card.onmouseout = function () {
			this.style.transform = "translateY(0)";
			this.style.boxShadow = "0 4px 16px rgba(0,0,0,0.06)";
		};
		card.onclick = function () {
			map.setCenter({ lat: p.lat, lng: p.lng });
			map.setZoom(16);
			window.scrollTo({ top: 0, behavior: "smooth" });
		};

		const statusColor = p.status === "OPERATIONAL" ? "#16a34a" : p.status === "CLOSED_PERMANENTLY" || p.status === "CLOSED_TEMPORARILY" ? "#dc2626" : "#d97706";
		const statusLabel = p.status === "OPERATIONAL" ? "Open" : p.status === "CLOSED_PERMANENTLY" ? "Permanently Closed" : p.status === "CLOSED_TEMPORARILY" ? "Temporarily Closed" : p.status;

		card.innerHTML = `
			<div style="display:flex; justify-content:space-between; align-items:start; margin-bottom:0.6rem;">
				<h5 style="margin:0; font-size:1rem; font-weight:700; color:#0F172A; flex:1;">
					<i class="fas fa-building" style="color:#C5A059; margin-right:0.5rem; font-size:0.9rem;"></i>${p.name}
				</h5>
				<span style="background:linear-gradient(135deg,#0F172A,#1E293B); color:#C5A059;
							 font-size:0.75rem; font-weight:700; padding:4px 10px; border-radius:20px;
							 white-space:nowrap; margin-left:0.8rem;">
					${p.distText}
				</span>
			</div>
			<p style="margin:0 0 0.5rem; font-size:0.88rem; color:#64748B; line-height:1.5;">
				<i class="fas fa-map-pin" style="color:#94A3B8; margin-right:0.4rem;"></i>${p.address}
			</p>
			<div style="display:flex; align-items:center; gap:1rem; font-size:0.82rem; color:#64748B; flex-wrap:wrap;">
				<span>
					<i class="fas fa-car" style="color:#C5A059; margin-right:0.3rem;"></i>${p.timeText}
				</span>
				<span>
					<i class="fas fa-circle" style="color:${statusColor}; font-size:0.5rem; margin-right:0.3rem; vertical-align:middle;"></i>
					<span style="color:${statusColor}; font-weight:600;">${statusLabel}</span>
				</span>
				<span style="margin-left:auto;">
					<a href="https://www.google.com/maps/dir/?api=1&destination=${p.lat},${p.lng}"
					   target="_blank" style="color:#C5A059; font-weight:600; text-decoration:none;"
					   onclick="event.stopPropagation();">
						<i class="fas fa-directions" style="margin-right:0.3rem;"></i>Directions
					</a>
				</span>
			</div>
		`;

		grid.appendChild(card);
	});
}



window.initmap = initmap();

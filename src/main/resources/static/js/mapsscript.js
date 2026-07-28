console.log("this is mapsscript")

let map, infoWindow;
let globalMarkers = [];
let globalPlaceData = [];
let globalCurrentPos = null;
let globalAdvancedMarkerElement = null;
let currentFilter = 'all';

async function applyFilter(filterType) {
	currentFilter = filterType;
	// Update buttons
	document.querySelectorAll('.filter-btn').forEach(btn => {
		if (btn.dataset.filter === filterType) {
			btn.classList.add('active');
			btn.style.backgroundColor = '#C5A059';
			btn.style.color = 'white';
		} else {
			btn.classList.remove('active');
			btn.style.backgroundColor = 'transparent';
			btn.style.color = '#C5A059';
		}
	});
	renderPlaces();
}

function renderPlaces() {
	const grid = document.getElementById("place-cards-grid");
	const loading = document.getElementById("cards-loading");
	const empty = document.getElementById("cards-empty");
	const filterButtons = document.getElementById("place-filter-buttons");

	if (grid) grid.innerHTML = "";
	if (loading) loading.style.display = "none";

	// Clear old markers
	globalMarkers.forEach(m => m.map = null);
	globalMarkers = [];

	let filteredData = globalPlaceData;
	if (currentFilter === 'official') {
		filteredData = globalPlaceData.filter(p => !p.isInternetCenter);
	} else if (currentFilter === 'non-official') {
		filteredData = globalPlaceData.filter(p => p.isInternetCenter);
	}

	if (!filteredData || !filteredData.length) {
		if (empty) empty.style.display = "block";
		if (filterButtons) filterButtons.style.display = globalPlaceData.length > 0 ? "flex" : "none";
		return;
	}

	if (empty) empty.style.display = "none";
	if (filterButtons) filterButtons.style.display = "flex";

	const bounds = new google.maps.LatLngBounds();
	let validMarkers = 0;

	filteredData.forEach((p) => {
		// Marker
		if (globalAdvancedMarkerElement) {
			const marker = new globalAdvancedMarkerElement({
				map,
				position: { lat: p.lat, lng: p.lng },
				title: p.name || "Service Centre",
				gmpClickable: true,
			});
			globalMarkers.push(marker);
		} else {
			const marker = new google.maps.Marker({
				map,
				position: { lat: p.lat, lng: p.lng },
				title: p.name || "Service Centre",
			});
			globalMarkers.push(marker);
		}
		bounds.extend({ lat: p.lat, lng: p.lng });
		validMarkers++;

		// Card
		if (!grid) return;
		const card = document.createElement("div");
		const borderAccent = p.isInternetCenter ? "#2563eb" : "#C5A059";
		const badgeBg = p.isInternetCenter ? "#eff6ff" : "#fef3c7";
		const badgeColor = p.isInternetCenter ? "#1d4ed8" : "#92400e";
		const badgeText = p.isInternetCenter ? '<i class="fas fa-desktop mr-1"></i>Internet / CSC Center' : '<i class="fas fa-landmark mr-1"></i>Official Center';

		card.style.cssText = `
			background: #ffffff;
			border: 1px solid #e2e8f0;
			border-left: 5px solid ${borderAccent};
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
			<div style="display:flex; justify-content:space-between; align-items:start; margin-bottom:0.4rem;">
				<h5 style="margin:0; font-size:1rem; font-weight:700; color:#0F172A; flex:1;">
					<i class="${p.isInternetCenter ? 'fas fa-laptop-code' : 'fas fa-building'}" style="color:${borderAccent}; margin-right:0.5rem; font-size:0.9rem;"></i>${p.name}
				</h5>
				<span style="background:linear-gradient(135deg,#0F172A,#1E293B); color:#C5A059;
							 font-size:0.75rem; font-weight:700; padding:4px 10px; border-radius:20px;
							 white-space:nowrap; margin-left:0.8rem;">
					${p.distText}
				</span>
			</div>
			<div style="margin-bottom:0.6rem;">
				<span style="background:${badgeBg}; color:${badgeColor}; font-size:0.75rem; font-weight:600; padding:2px 8px; border-radius:6px; display:inline-block;">
					${badgeText}
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

	if (validMarkers > 0) {
		map.fitBounds(bounds);
	}
}

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

	// Initialize filter button listeners
	document.addEventListener('click', function (e) {
		if (e.target.classList.contains('filter-btn')) {
			const filter = e.target.getAttribute('data-filter');
			applyFilter(filter);
		}
	});

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
	const section = document.getElementById("place-cards-section");
	const grid = document.getElementById("place-cards-grid");
	const loading = document.getElementById("cards-loading");
	const empty = document.getElementById("cards-empty");
	section.style.display = "block";
	loading.style.display = "block";
	grid.innerHTML = "";
	empty.style.display = "none";

	const rawQuery = legalplaces ? legalplaces.textContent : "Government Office";

	// Helper to extract LatLngLiteral safely from Google Places objects
	function getPlaceCoordinates(place) {
		if (!place) return null;
		let loc = place.location || (place.geometry ? place.geometry.location : null);
		if (!loc) return null;
		let lat, lng;
		if (typeof loc.lat === "function") {
			lat = loc.lat();
			lng = loc.lng();
		} else if (typeof loc.lat === "number") {
			lat = loc.lat;
			lng = loc.lng;
		} else {
			return null;
		}
		return { lat: Number(lat), lng: Number(lng) };
	}

	// Prepare list of queries to run
	let queries = [];
	if (rawQuery.includes(" or ")) {
		queries = rawQuery.split(" or ").map(q => q.trim());
	} else {
		queries = [rawQuery];
	}
	// Add internet / CSC center queries
	queries.push("CSC Common Service Centre");
	queries.push("Internet Cafe Cyber Cafe");

	let allRawPlaces = [];

	// 1. Try Modern Places API (Place.searchByText) first
	try {
		const { Place } = await google.maps.importLibrary("places");
		const searchPromises = queries.map(q => {
			const req = {
				textQuery: q,
				fields: ["displayName", "location", "businessStatus", "formattedAddress", "id"],
				locationBias: { center: pos, radius: 10000 },
				language: "en-US",
			};
			return Place.searchByText(req).then(res => {
				const isInternet = q.toLowerCase().includes("csc") || q.toLowerCase().includes("internet") || q.toLowerCase().includes("cyber");
				return (res.places || []).map(p => ({
					name: p.displayName,
					location: p.location,
					address: p.formattedAddress,
					status: p.businessStatus,
					id: p.id,
					isInternetCenter: isInternet
				}));
			}).catch(err => {
				console.warn("Modern Places searchByText failed for query:", q, err);
				return [];
			});
		});

		const results = await Promise.all(searchPromises);
		allRawPlaces = results.flat();
	} catch (e) {
		console.warn("Modern Places API searchByText unavailable, trying PlacesService fallback...", e);
	}

	// 2. Fallback to classic PlacesService if no results from modern API
	if (!allRawPlaces.length && window.google && google.maps && google.maps.places && google.maps.places.PlacesService) {
		const service = new google.maps.places.PlacesService(map);
		const classicPromises = queries.map(q => {
			return new Promise((resolve) => {
				const isInternet = q.toLowerCase().includes("csc") || q.toLowerCase().includes("internet") || q.toLowerCase().includes("cyber");
				service.textSearch({
					location: pos,
					radius: 10000,
					query: q
				}, (results, status) => {
					if (status === google.maps.places.PlacesServiceStatus.OK && results) {
						resolve(results.map(p => ({
							name: p.name,
							location: p.geometry ? p.geometry.location : null,
							address: p.formatted_address || p.vicinity,
							status: p.business_status || "OPERATIONAL",
							id: p.place_id,
							isInternetCenter: isInternet
						})));
					} else {
						resolve([]);
					}
				});
			});
		});
		const classicResults = await Promise.all(classicPromises);
		allRawPlaces = classicResults.flat();
	}

	// Deduplicate places
	const combinedMap = new Map();
	allRawPlaces.forEach(p => {
		const coords = getPlaceCoordinates(p);
		if (!coords) return;
		const key = p.id || (coords.lat.toFixed(4) + "_" + coords.lng.toFixed(4));
		if (!combinedMap.has(key)) {
			combinedMap.set(key, p);
		}
	});

	const places = Array.from(combinedMap.values());

	if (!places || !places.length) {
		loading.style.display = "none";
		empty.style.display = "block";
		return;
	}

	// Haversine Distance calculation
	function haversineDistance(p1, p2) {
		const R = 6371; // km
		const dLat = (p2.lat - p1.lat) * Math.PI / 180;
		const dLng = (p2.lng - p1.lng) * Math.PI / 180;
		const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
			Math.cos(p1.lat * Math.PI / 180) * Math.cos(p2.lat * Math.PI / 180) *
			Math.sin(dLng / 2) * Math.sin(dLng / 2);
		return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}

	// Format Place Data
	globalCurrentPos = pos;
	globalPlaceData = places.map((place) => {
		const coords = getPlaceCoordinates(place);
		if (!coords) return null;
		const distKm = haversineDistance(pos, coords);
		const estMinutes = Math.round((distKm / 30) * 60);
		return {
			name: place.name || "Service Centre",
			address: place.address || "Address not available",
			status: place.status || "UNKNOWN",
			distKm: distKm,
			distText: distKm < 1 ? (distKm * 1000).toFixed(0) + " m" : distKm.toFixed(1) + " km",
			timeText: estMinutes < 1 ? "< 1 min" : estMinutes + " min (approx)",
			lat: coords.lat,
			lng: coords.lng,
			isInternetCenter: place.isInternetCenter,
		};
	}).filter(Boolean);

	// Sort closest first
	globalPlaceData.sort((a, b) => a.distKm - b.distKm);

	// Fetch AdvancedMarkerElement if not fetched
	try {
		const markerLib = await google.maps.importLibrary("marker");
		globalAdvancedMarkerElement = markerLib.AdvancedMarkerElement;
	} catch (e) {
		console.warn("AdvancedMarkerElement unavailable", e);
	}

	renderPlaces();
}



window.initmap = initmap();

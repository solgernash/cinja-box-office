/************************************************************
 * Cinema Ninja Frontend
 * ----------------------------------------------------------
 * This file handles:
 * - Fetching movies from the Spring Boot backend
 * - Normalizing MongoDB movie data for the frontend
 * - Rendering movie cards
 * - Search and genre filtering
 * - Movie details modal
 * - Showtime-to-booking scroll behavior
 * - Ticket quantity controls
 * - Seat selection
 * - Booking total calculation
 * - Scroll animations
 ************************************************************/


/************************************************************
 * Global movie state
 * ----------------------------------------------------------
 * Start empty. Movies should come from MongoDB through the
 * backend API, not from hardcoded frontend data.
 ************************************************************/
let movies = [];


/************************************************************
 * DOM references
 * ----------------------------------------------------------
 * These grab the HTML elements that app.js needs to update.
 ************************************************************/
const runningMovies = document.querySelector("#runningMovies");
const comingMovies = document.querySelector("#comingMovies");
const searchInput = document.querySelector("#searchInput");
const genreFilter = document.querySelector("#genreFilter");
const emptyState = document.querySelector("#emptyState");
const movieDialog = document.querySelector("#movieDialog");
const dialogContent = document.querySelector("#dialogContent");
const closeDialog = document.querySelector("#closeDialog");
const openAuthDialog = document.querySelector("#openAuthDialog");
const authDialog = document.querySelector("#authDialog");
const closeAuthDialog = document.querySelector("#closeAuthDialog");
const authTitle = document.querySelector("#authTitle");
const authMessage = document.querySelector("#authMessage");
const profileDialog = document.querySelector("#profileDialog");
const closeProfileDialog = document.querySelector("#closeProfileDialog");
const profileContent = document.querySelector("#profileContent");
const editProfileButton = document.querySelector("#editProfileButton");
const logoutButton = document.querySelector("#logoutButton");
const adminPortal = document.querySelector("#admin-portal");
const adminWelcome = document.querySelector("#adminWelcome");
const backToCinema = document.querySelector("#backToCinema");
const siteHeader = document.querySelector(".site-header");
const scrollProgress = document.querySelector(".scroll-progress");



/************************************************************
 * Backend configuration
 * ----------------------------------------------------------
 * Change this if your Spring Boot route changes.
 ************************************************************/
const MOVIES_API_URL = "http://localhost:8081/api/movies";
const AUTH_API_URL = "http://localhost:8081/api/auth";
const USERS_API_URL = "http://localhost:8081/api/users";

let currentUser = null;
let favoriteMovieIds = new Set();


/************************************************************
 * MongoDB/backend data normalization
 * ----------------------------------------------------------
 * Your MongoDB documents look like this:
 *
 * {
 *   title: "Shadow Circuit",
 *   runtime: "2h 04m",
 *   genre: "Action",
 *   rating: "PG-13",
 *   description: "...",
 *   posterUrl: "...",
 *   trailerUrl: "...",
 *   status: "RUNNING",
 *   showtimes: ["2:00 PM", "5:00 PM", "8:00 PM"]
 * }
 *
 * The frontend expects:
 *
 * {
 *   title,
 *   runtime,
 *   genre,
 *   rating,
 *   description,
 *   poster,
 *   trailer,
 *   status: "Currently Running" or "Coming Soon",
 *   showtimes: [...]
 * }
 ************************************************************/
function normalizeMovieStatus(status) {
  if (!status) {
    return "Coming Soon";
  }

  const normalized = String(status)
    .toLowerCase()
    .replace(/_/g, " ")
    .trim();

  if (
    normalized === "running" ||
    normalized === "currently running" ||
    normalized === "now playing"
  ) {
    return "Currently Running";
  }

  if (
    normalized === "coming soon" ||
    normalized === "coming" ||
    normalized === "soon"
  ) {
    return "Coming Soon";
  }

  return status;
}


function normalizeShowtimes(showtimes) {
  if (Array.isArray(showtimes) && showtimes.length > 0) {
    return showtimes;
  }

  if (typeof showtimes === "string" && showtimes.trim()) {
    return showtimes.split(",").map((time) => time.trim());
  }

  return ["Coming Soon"];
}

function normalizeTrailerUrl(trailerUrl) {
  if (!trailerUrl) {
    return "";
  }

  if (trailerUrl.includes("youtube.com/watch?v=")) {
    return trailerUrl.replace("watch?v=", "embed/");
  }

  if (trailerUrl.includes("youtu.be/")) {
    const videoId = trailerUrl.split("youtu.be/")[1].split("?")[0];
    return `https://www.youtube.com/embed/${videoId}`;
  }

  return trailerUrl;
}

function normalizeMovieFromBackend(movie) {
  return {
    id: movie.id || movie._id?.$oid || movie._id || movie.movieId,
    title: movie.title || "Untitled Movie",
    genre: movie.genre || "Unknown",
    rating: movie.rating || "NR",
    status: normalizeMovieStatus(movie.status),
    runtime: movie.runtime || "Runtime TBD",
    showtimes: normalizeShowtimes(movie.showtimes),
    description: movie.description || "No description available.",
    poster: movie.posterUrl || movie.poster || movie.imageUrl || "",
    trailer: normalizeTrailerUrl(movie.trailerUrl || movie.trailer || "")
  };
}


/************************************************************
 * Fetch movies from backend
 * ----------------------------------------------------------
 * This is the only place where movies should be loaded.
 * The page renders after the fetch completes.
 ************************************************************/
async function fetchMoviesFromBackend() {
  try {
    if (emptyState) {
      emptyState.hidden = false;
      emptyState.textContent = "Loading movies from database...";
    }

    const response = await fetch(MOVIES_API_URL);

    if (!response.ok) {
      throw new Error(`Backend returned ${response.status}`);
    }

    const data = await response.json();

    console.log("Raw movies from backend:", data);

    movies = Array.isArray(data)
      ? data.map(normalizeMovieFromBackend)
      : [];

    console.log("Normalized movies:", movies);

    resetGenres();
    loadGenres();
    renderMovies();
  } catch (error) {
    console.error("Error fetching movies from backend:", error);

    movies = [];
    resetGenres();
    renderMovies();

    if (emptyState) {
      emptyState.hidden = false;
      emptyState.textContent =
        "Could not load movies from the backend. Check the backend server and movie API.";
    }
  }
}


function showLoadingMessage() {
  runningMovies.innerHTML = "";
  comingMovies.innerHTML = "";

  if (emptyState) {
    emptyState.hidden = false;
    emptyState.textContent = "Loading movies from Cinema Ninja database...";
  }
}

function getCurrentUserId() {
  return (
    currentUser?.userId ||
    currentUser?.id ||
    currentUser?._id?.$oid ||
    currentUser?._id ||
    ""
  );
}

function getMovieId(movie) {
  return (
    movie?.id ||
    movie?._id?.$oid ||
    movie?._id ||
    movie?.movieId ||
    ""
  );
}

async function apiRequest(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    }
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(
      data.message ||
      data.error ||
      `Request failed with status ${response.status}`
    );
  }

  return data;
}

function normalizeProfileResponse(profile) {
  return {
    ...profile,
    id: profile.id || profile.userId || profile._id?.$oid || profile._id,
    address: profile.address || null,
    cards: profile.cards || profile.paymentCards || [],
    favorites: profile.favorites || profile.favoriteMovies || []
  };
}

async function loadFullUserProfile() {
  const userId = getCurrentUserId();

  if (!userId) {
    throw new Error("No logged-in user found.");
  }

  const profile = await apiRequest(`${USERS_API_URL}/${userId}`);
  const cards = await apiRequest(`${USERS_API_URL}/${userId}/cards`);
  const favorites = await apiRequest(`${USERS_API_URL}/${userId}/favorites`);

  currentUser = normalizeProfileResponse({
    ...currentUser,
    ...profile,
    cards,
    favorites
  });

  favoriteMovieIds = new Set(
    favorites.map((movie) => String(getMovieId(movie))).filter(Boolean)
  );

  renderMovies();

  return currentUser;
}

function getCardDisplayNumber(card) {
  return (
    card.maskedCardNumber ||
    card.lastFour ||
    card.cardLastFour ||
    "••••"
  );
}

function renderPaymentCards(cards) {
  if (!cards || cards.length === 0) {
    return `<p class="profile-muted">No payment cards saved yet.</p>`;
  }

  return `
    <div class="credit-card-grid">
      ${cards
        .map((card, index) => {
          const cardId = card.id || card.cardId || card._id?.$oid || card._id;
          const displayNumber = getCardDisplayNumber(card);
          const formattedNumber = displayNumber.includes("•")
            ? displayNumber
            : `•••• •••• •••• ${displayNumber}`;

          return `
            <article class="saved-credit-card">
              <div class="credit-card-topline">
                <span class="credit-card-brand">CINJA CARD</span>
                <span class="credit-card-chip"></span>
              </div>

              <div class="credit-card-number">
                ${escapeHtml(formattedNumber)}
              </div>

              <div class="credit-card-bottom">
                <div>
                  <span class="credit-card-label">Cardholder</span>
                  <strong>${escapeHtml(card.cardholderName || "Cardholder")}</strong>
                </div>

                <div>
                  <span class="credit-card-label">Expires</span>
                  <strong>${escapeHtml(card.expirationDate || "N/A")}</strong>
                </div>
              </div>

              <button
                class="credit-card-remove"
                type="button"
                data-delete-card-id="${escapeHtml(String(cardId || ""))}"
              >
                Remove
              </button>
            </article>
          `;
        })
        .join("")}
    </div>
  `;
}


/************************************************************
 * Genre filter helpers
 ************************************************************/
function resetGenres() {
  genreFilter.innerHTML = `<option value="all">All genres</option>`;
}


function loadGenres() {
  const genres = [...new Set(movies.map((movie) => movie.genre))]
    .filter(Boolean)
    .sort();

  genres.forEach((genre) => {
    const option = document.createElement("option");
    option.value = genre;
    option.textContent = genre;
    genreFilter.appendChild(option);
  });
}


/************************************************************
 * Fuzzy search helpers
 * ----------------------------------------------------------
 * This allows imperfect searches like:
 * - "shado circut" -> Shadow Circuit
 * - "neon shurkin" -> Neon Shuriken
 ************************************************************/
function normalizeText(text) {
  return String(text || "")
    .toLowerCase()
    .replace(/[^a-z0-9\s]/g, "")
    .replace(/\s+/g, " ")
    .trim();
}


function levenshteinDistance(a, b) {
  const matrix = [];

  for (let row = 0; row <= b.length; row++) {
    matrix[row] = [row];
  }

  for (let column = 0; column <= a.length; column++) {
    matrix[0][column] = column;
  }

  for (let row = 1; row <= b.length; row++) {
    for (let column = 1; column <= a.length; column++) {
      if (b.charAt(row - 1) === a.charAt(column - 1)) {
        matrix[row][column] = matrix[row - 1][column - 1];
      } else {
        matrix[row][column] = Math.min(
          matrix[row - 1][column - 1] + 1,
          matrix[row][column - 1] + 1,
          matrix[row - 1][column] + 1
        );
      }
    }
  }

  return matrix[b.length][a.length];
}


function wordsAreClose(queryWord, titleWord) {
  if (!queryWord || !titleWord) {
    return false;
  }

  if (titleWord.includes(queryWord) || queryWord.includes(titleWord)) {
    return true;
  }

  const distance = levenshteinDistance(queryWord, titleWord);
  const allowedMistakes = Math.max(1, Math.floor(titleWord.length * 0.4));

  return distance <= allowedMistakes;
}


function fuzzyMatch(query, movie) {
  const normalizedQuery = normalizeText(query);

  if (!normalizedQuery) {
    return true;
  }

  const normalizedTitle = normalizeText(movie.title);
  const normalizedGenre = normalizeText(movie.genre);
  const searchableText = `${normalizedTitle} ${normalizedGenre}`;

  // Exact or partial matches should pass immediately.
  if (searchableText.includes(normalizedQuery)) {
    return true;
  }

  const queryWords = normalizedQuery.split(" ");
  const titleWords = normalizedTitle.split(" ");
  const genreWords = normalizedGenre.split(" ");
  const searchableWords = [...titleWords, ...genreWords];

  // Every typed word needs to be close to at least one movie/genre word.
  return queryWords.every((queryWord) =>
    searchableWords.some((searchableWord) =>
      wordsAreClose(queryWord, searchableWord)
    )
  );
}


function getSearchScore(query, movie) {
  const normalizedQuery = normalizeText(query);

  if (!normalizedQuery) {
    return 0;
  }

  const normalizedTitle = normalizeText(movie.title);
  const normalizedGenre = normalizeText(movie.genre);

  if (normalizedTitle === normalizedQuery) return 100;
  if (normalizedTitle.startsWith(normalizedQuery)) return 90;
  if (normalizedTitle.includes(normalizedQuery)) return 80;
  if (normalizedGenre.includes(normalizedQuery)) return 60;

  const queryWords = normalizedQuery.split(" ");
  const titleWords = normalizedTitle.split(" ");

  let score = 0;

  queryWords.forEach((queryWord) => {
    const bestDistance = Math.min(
      ...titleWords.map((titleWord) =>
        levenshteinDistance(queryWord, titleWord)
      )
    );

    score += Math.max(0, 40 - bestDistance * 8);
  });

  return score;
}


/************************************************************
 * Filtering
 ************************************************************/
function matchesFilters(movie) {
  const searchTerm = searchInput.value.trim();
  const selectedGenre = genreFilter.value;

  const titleMatch = fuzzyMatch(searchTerm, movie);
  const genreMatch = selectedGenre === "all" || movie.genre === selectedGenre;

  return titleMatch && genreMatch;
}


/************************************************************
 * Movie card rendering
 ************************************************************/
function movieCard(movie) {
  const article = document.createElement("article");
  article.className = "movie-card";

  const fallbackPoster =
    "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=80";

  const posterUrl = movie.poster || fallbackPoster;

  const movieId = getMovieId(movie);
  const isFavorite = favoriteMovieIds.has(String(movieId));

  article.style.setProperty("--poster", `url('${posterUrl}')`);

  article.innerHTML = `
    <span class="status-pill">${movie.status}</span>

    <button
      class="favorite-button ${isFavorite ? "active" : ""}"
      type="button"
      data-favorite-movie-id="${movieId}"
      title="${isFavorite ? "Remove from favorites" : "Add to favorites"}"
      aria-label="${isFavorite ? "Remove from favorites" : "Add to favorites"}"
    >
      ${isFavorite ? "♥" : "♡"}
    </button>

    <h3>${movie.title}</h3>

    <div class="movie-meta">
      <span>${movie.genre}</span>
      <span>|</span>
      <span>${movie.rating}</span>
      <span>|</span>
      <span>${movie.runtime}</span>
    </div>

    <p>${movie.description}</p>

    <div class="showtimes">
      ${movie.showtimes
        .map(
          (time) =>
            `<button type="button" data-title="${movie.title}" data-time="${time}">${time}</button>`
        )
        .join("")}
    </div>

    <button class="details-button" type="button" data-details="${movie.title}">
      View Details + Trailer
    </button>
  `;

  return article;
}


function renderMovies() {
  runningMovies.innerHTML = "";
  comingMovies.innerHTML = "";

  const searchTerm = searchInput.value.trim();

  const filtered = movies
    .filter(matchesFilters)
    .sort(
      (a, b) => getSearchScore(searchTerm, b) - getSearchScore(searchTerm, a)
    );

  const filteredRunningMovies = filtered.filter(
    (movie) => movie.status === "Currently Running"
  );

  const filteredComingMovies = filtered.filter(
    (movie) => movie.status === "Coming Soon"
  );

  filteredRunningMovies.forEach((movie) => {
    runningMovies.appendChild(movieCard(movie));
  });

  filteredComingMovies.forEach((movie) => {
    comingMovies.appendChild(movieCard(movie));
  });

  if (emptyState) {
    emptyState.hidden = filtered.length > 0;
    emptyState.textContent = "No matching movies found. Adjust the search or genre filter.";
  }

  observeScrollAnimations();
}


/************************************************************
 * Movie details modal
 ************************************************************/
function openMovieDetails(movie) {
  const fallbackPoster =
    "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=80";

  const posterUrl = movie.poster || fallbackPoster;

  const trailerMarkup = movie.trailer
    ? `
      <iframe
        class="trailer-frame"
        src="${movie.trailer}"
        title="${movie.title} trailer"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
        allowfullscreen>
      </iframe>
    `
    : `<p class="empty-state">Trailer unavailable.</p>`;

  dialogContent.innerHTML = `
    <div class="dialog-layout">
      <div class="dialog-poster" style="--poster: url('${posterUrl}')"></div>

      <div class="dialog-info">
        <p class="section-kicker">${movie.genre} | ${movie.rating} | ${movie.runtime}</p>
        <h2 id="dialogTitle">${movie.title}</h2>
        <p>${movie.description}</p>

        <div class="showtimes">
          ${movie.showtimes
            .map(
              (time) =>
                `<button type="button" data-title="${movie.title}" data-time="${time}">${time}</button>`
            )
            .join("")}
        </div>

        ${trailerMarkup}
      </div>
    </div>
  `;

  movieDialog.showModal();
}


/************************************************************
 * Login and registration modal
 ************************************************************/
function setAuthMode(mode) {
  document.querySelectorAll("[data-auth-tab]").forEach((tab) => {
    const isActive = tab.dataset.authTab === mode;
    tab.classList.toggle("active", isActive);
    tab.setAttribute("aria-selected", String(isActive));
  });

  document.querySelectorAll("[data-auth-form]").forEach((form) => {
    const isActive = form.dataset.authForm === mode;
    form.classList.toggle("active", isActive);
    form.hidden = !isActive;
  });

  if (authMessage) {
    authMessage.textContent = "";
  }

  if (authTitle) {
    const titles = {
      forgot: "Forgot Password",
      login: "Login",
      register: "Register",
      reset: "Reset Password"
    };

    authTitle.textContent = titles[mode] || "Login";
  }
}


function openAuthModal() {
  setAuthMode("login");
  authDialog.showModal();
}


function updateAuthButtonForUser(user) {
  const isLoggedIn = Boolean(user);

  openAuthDialog.classList.toggle("logged-in", isLoggedIn);
  openAuthDialog.setAttribute(
    "aria-label",
    isLoggedIn ? "Open profile" : "Open login form"
  );
  openAuthDialog.title = isLoggedIn ? "Profile" : "Login";
}


function getAuthErrorMessage(errorBody, fallbackMessage, status) {
  if (errorBody && typeof errorBody.message === "string") {
    return errorBody.message;
  }

  if (status === 401) {
    return "Invalid email or password. Please try again.";
  }

  if (status === 403) {
    return "Login blocked. Please verify your account or contact support.";
  }

  if (status === 409) {
    return "An account with this email already exists.";
  }

  if (status === 400) {
    return fallbackMessage;
  }

  if (errorBody && typeof errorBody.error === "string") {
    return errorBody.error;
  }

  return fallbackMessage;
}


function escapeHtml(value) {
  return String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}


async function submitRegistration(form) {
  const firstName = form.querySelector("#registerFirstName").value.trim();
  const lastName = form.querySelector("#registerLastName").value.trim();

  if (!firstName || !lastName) {
    throw new Error("Please enter your first and last name.");
  }

  const payload = {
    firstName,
    lastName,
    email: form.querySelector("#registerEmail").value,
    password: form.querySelector("#registerPassword").value,
    promotions: false
  };

  const response = await fetch(`${AUTH_API_URL}/register`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(
      getAuthErrorMessage(data, "Registration failed. Please check your entries.", response.status)
    );
  }

  return data.message || "Account created. Please check your email to verify your account.";
}


async function submitLogin(form) {
  const payload = {
    email: form.querySelector("#loginEmail").value,
    password: form.querySelector("#loginPassword").value
  };

  const response = await fetch(`${AUTH_API_URL}/login`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(
      getAuthErrorMessage(data, "Login failed. Please try again.", response.status)
    );
  }

  return data;
}


async function submitForgotPassword(form) {
  const payload = {
    email: form.querySelector("#forgotEmail").value
  };

  const response = await fetch(`${AUTH_API_URL}/forgot-password`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(
      getAuthErrorMessage(data, "Could not request password reset. Please try again.", response.status)
    );
  }

  return data.message || "If an account exists for that email, a reset link has been sent.";
}


async function submitPasswordReset(form) {
  const payload = {
    token: form.querySelector("#resetToken").value.trim(),
    newPassword: form.querySelector("#resetPassword").value
  };

  const response = await fetch(`${AUTH_API_URL}/reset-password`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(
      getAuthErrorMessage(data, "Could not update password. Please try again.", response.status)
    );
  }

  return data.message || "Password has been reset. You can now log in.";
}


async function handleAuthSubmit(event) {
  event.preventDefault();

  const form = event.target;
  const mode = form.dataset.authForm;
  const submitButton = form.querySelector("button[type='submit']");

  if (authMessage) {
    const loadingMessages = {
      forgot: "Sending reset link...",
      login: "Logging you in...",
      register: "Creating your account...",
      reset: "Updating your password..."
    };

    authMessage.textContent = loadingMessages[mode] || "Working...";
  }

  if (submitButton) {
    submitButton.disabled = true;
  }

  try {
    const submitters = {
      forgot: submitForgotPassword,
      login: submitLogin,
      register: submitRegistration,
      reset: submitPasswordReset
    };

    const result = await submitters[mode](form);
    const message = mode === "login"
      ? handleSuccessfulLogin(result)
      : result;

    if (authMessage) {
      authMessage.textContent = message;
    }

    form.reset();

    if (mode === "reset") {
      setAuthMode("login");
      authMessage.textContent = message;
    }
  } catch (error) {
    if (authMessage) {
      authMessage.textContent = error.message;
    }
  } finally {
    if (submitButton) {
      submitButton.disabled = false;
    }
  }
}


function showAdminPortal(user) {
  document.querySelectorAll(".customer-view").forEach((section) => {
    section.hidden = true;
  });

  if (adminWelcome) {
    adminWelcome.textContent = `Signed in as ${user.firstName || "Admin"} ${user.lastName || ""}`.trim();
  }

  if (adminPortal) {
    adminPortal.hidden = false;
    window.location.hash = "#admin-portal";
    adminPortal.scrollIntoView({ behavior: "smooth", block: "start" });
  }
}


function showCustomerHome(user) {
  if (adminPortal) {
    adminPortal.hidden = true;
  }

  document.querySelectorAll(".customer-view").forEach((section) => {
    section.hidden = false;
  });

  window.location.hash = "#top";
  window.scrollTo({ top: 0, behavior: "smooth" });

  return `Welcome back, ${user.firstName || "CINJA member"}.`;
}


function handleSuccessfulLogin(user) {
  currentUser = user;
  updateNavbarGreeting(currentUser);
  updateAuthButtonForUser(user);

  loadFullUserProfile().catch((error) => {
    console.error("Could not load full user profile:", error);
  });

  if (authDialog && authDialog.open) {
    authDialog.close();
  }

  if (user.role === "ADMIN") {
    showAdminPortal(user);
    return `Welcome back, ${user.firstName || "Admin"}. Opening admin portal.`;
  }

  return showCustomerHome(user);
}


function handleAuthButtonClick() {
  if (!currentUser) {
    openAuthModal();
    return;
  }

  openProfileModal();
}


async function getCurrentUserProfile() {
  if (!getCurrentUserId()) {
    return currentUser;
  }

  return loadFullUserProfile();
}


function renderProfile(user) {
  const displayName =
    `${user.firstName || ""} ${user.lastName || ""}`.trim() || "CINJA Member";

  const role = user.role || "CUSTOMER";
  const address = user.address;
  const cards = user.cards || [];
  const favorites = user.favorites || [];

  profileContent.innerHTML = `
    <section class="profile-section">
      <h3>Account Information</h3>

      <dl class="profile-details">
        <div>
          <dt>Name</dt>
          <dd>${escapeHtml(displayName)}</dd>
        </div>

        <div>
          <dt>Email</dt>
          <dd>${escapeHtml(user.email || "Not available")}</dd>
        </div>

        <div>
          <dt>Role</dt>
          <dd>${escapeHtml(role)}</dd>
        </div>
      </dl>
    </section>

    <section class="profile-section">
      <div class="profile-section-header">
        <h3>Address</h3>
        <button class="secondary-button small-profile-button" type="button" data-profile-action="edit-address">
          ${address ? "Edit Address" : "Add Address"}
        </button>
      </div>

      ${
        address
          ? `
            <div class="profile-card">
              <p>${escapeHtml(address.street || "")}</p>
              <p>
                ${escapeHtml(address.city || "")},
                ${escapeHtml(address.state || "")}
                ${escapeHtml(address.zipCode || "")}
              </p>
            </div>
          `
          : `<p class="profile-muted">No address saved yet.</p>`
      }
    </section>

    <section class="profile-section">
      <div class="profile-section-header">
        <h3>Payment Cards</h3>
        ${
          cards.length < 3
            ? `
              <button class="secondary-button small-profile-button" type="button" data-profile-action="add-card">
                Add Card
              </button>
            `
            : `<span class="profile-limit-note">Card limit reached: 3/3</span>`
        }
      </div>

      ${renderPaymentCards(cards)}
    </section>

    <section class="profile-section">
      <h3>Favorite Movies</h3>

      ${
        favorites.length > 0
          ? `
            <div class="favorite-list">
              ${favorites
                .map((movie) => {
                  const movieId = getMovieId(movie);

                  return `
                    <div class="profile-card profile-card-row">
                      <div>
                        <strong>${escapeHtml(movie.title || "Untitled Movie")}</strong>
                        <p>${escapeHtml(movie.genre || "Movie")}</p>
                      </div>

                      <button
                        class="danger-button"
                        type="button"
                        data-remove-favorite-id="${escapeHtml(String(movieId || ""))}"
                      >
                        Remove
                      </button>
                    </div>
                  `;
                })
                .join("")}
            </div>
          `
          : `<p class="profile-muted">No favorite movies yet. Use the heart button while browsing movies.</p>`
      }
    </section>
  `;

  editProfileButton.hidden = false;
}


function renderEditProfileForm(user) {
  profileContent.innerHTML = `
    <form id="editProfileForm" class="auth-form profile-edit-form">
      <label for="profileFirstName">First Name</label>
      <input id="profileFirstName" type="text" value="${escapeHtml(user.firstName || "")}" autocomplete="given-name" required>

      <label for="profileLastName">Last Name</label>
      <input id="profileLastName" type="text" value="${escapeHtml(user.lastName || "")}" autocomplete="family-name" required>

      <label for="profileEmail">Email</label>
      <input id="profileEmail" type="email" value="${escapeHtml(user.email || "")}" disabled>

      <div class="profile-edit-actions">
        <button class="primary-button" type="submit">Save Changes</button>
        <button class="secondary-button" id="cancelProfileEdit" type="button">Cancel</button>
      </div>
    </form>
    <p id="profileMessage" class="auth-message" aria-live="polite"></p>
  `;

  editProfileButton.hidden = true;
}

function updateNavbarGreeting(user) {
  let greeting = document.querySelector("#navbarGreeting");

  if (!greeting) {
    greeting = document.createElement("span");
    greeting.id = "navbarGreeting";
    greeting.className = "navbar-greeting";

    const header = document.querySelector(".site-header");
    const rightSideButton =
      document.querySelector("#authButton") ||
      document.querySelector("#profileButton") ||
      document.querySelector(".ghost-button");

    if (header && rightSideButton) {
      header.insertBefore(greeting, rightSideButton);
    } else if (header) {
      header.appendChild(greeting);
    }
  }

  if (!user) {
    greeting.hidden = true;
    greeting.textContent = "";
    return;
  }

  const firstName = user.firstName || user.name?.split(" ")[0] || "there";

  greeting.hidden = false;
  greeting.textContent = `Hello, ${firstName}`;
}

function renderAddressForm(user) {
  const address = user.address || {};

  profileContent.innerHTML = `
    <form id="addressForm" class="auth-form profile-edit-form">
      <h3>Address</h3>

      <label for="addressStreet">Street <span class="required">*</span></label>
      <input id="addressStreet" type="text" value="${escapeHtml(address.street || "")}" required>

      <label for="addressCity">City <span class="required">*</span></label>
      <input id="addressCity" type="text" value="${escapeHtml(address.city || "")}" required>

      <label for="addressState">State <span class="required">*</span></label>
      <input id="addressState" type="text" value="${escapeHtml(address.state || "")}" required>

      <label for="addressZipCode">ZIP Code <span class="required">*</span></label>
      <input id="addressZipCode" type="text" value="${escapeHtml(address.zipCode || "")}" required>

      <div class="profile-edit-actions">
        <button class="primary-button" type="submit">Save Address</button>
        <button class="secondary-button" type="button" data-profile-action="cancel-profile-panel">Cancel</button>
      </div>

      <p id="profileMessage" class="auth-message" aria-live="polite"></p>
    </form>
  `;

  editProfileButton.hidden = true;
}

function renderCardForm() {
  profileContent.innerHTML = `
    <form id="cardForm" class="auth-form profile-edit-form">
      <h3>Add Payment Card</h3>

      <label for="cardholderName">Cardholder Name <span class="required">*</span></label>
      <input id="cardholderName" type="text" autocomplete="cc-name" required>

      <label for="cardNumber">Card Number <span class="required">*</span></label>
      <input id="cardNumber" type="text" inputmode="numeric" autocomplete="cc-number" placeholder="1234123412341234" required>

      <label for="expirationDate">Expiration Date <span class="required">*</span></label>
      <input id="expirationDate" type="text" autocomplete="cc-exp" placeholder="MM/YY" required>

      <div class="profile-edit-actions">
        <button class="primary-button" type="submit">Save Card</button>
        <button class="secondary-button" type="button" data-profile-action="cancel-profile-panel">Cancel</button>
      </div>

      <p id="profileMessage" class="auth-message" aria-live="polite"></p>
    </form>
  `;

  editProfileButton.hidden = true;
}

async function saveAddress(event) {
  event.preventDefault();

  const userId = getCurrentUserId();
  const form = event.target;
  const profileMessage = document.querySelector("#profileMessage");

  const payload = {
    street: form.querySelector("#addressStreet").value.trim(),
    city: form.querySelector("#addressCity").value.trim(),
    state: form.querySelector("#addressState").value.trim(),
    zipCode: form.querySelector("#addressZipCode").value.trim()
  };

  if (!payload.street || !payload.city || !payload.state || !payload.zipCode) {
    profileMessage.textContent = "All address fields are required.";
    return;
  }

  try {
    profileMessage.textContent = "Saving address...";

    await apiRequest(`${USERS_API_URL}/${userId}/address`, {
      method: "PUT",
      body: JSON.stringify(payload)
    });

    await loadFullUserProfile();
    renderProfile(currentUser);

    profileContent.insertAdjacentHTML(
      "beforeend",
      `<p class="auth-message">Address saved successfully.</p>`
    );
  } catch (error) {
    profileMessage.textContent = error.message;
  }
}

async function saveCard(event) {
  event.preventDefault();

  const userId = getCurrentUserId();
  const form = event.target;
  const profileMessage = document.querySelector("#profileMessage");

  const payload = {
    cardholderName: form.querySelector("#cardholderName").value.trim(),
    cardNumber: form.querySelector("#cardNumber").value.trim(),
    expirationDate: form.querySelector("#expirationDate").value.trim()
  };

  if (!payload.cardholderName || !payload.cardNumber || !payload.expirationDate) {
    profileMessage.textContent = "All card fields are required.";
    return;
  }

  if ((currentUser.cards || []).length >= 3) {
    profileMessage.textContent = "You can only store up to 3 payment cards.";
    return;
  }

  try {
    profileMessage.textContent = "Saving payment card...";

    await apiRequest(`${USERS_API_URL}/${userId}/cards`, {
      method: "POST",
      body: JSON.stringify(payload)
    });

    await loadFullUserProfile();
    renderProfile(currentUser);

    profileContent.insertAdjacentHTML(
      "beforeend",
      `<p class="auth-message">Payment card saved successfully.</p>`
    );
  } catch (error) {
    profileMessage.textContent = error.message;
  }
}

async function deleteCard(cardId) {
  const userId = getCurrentUserId();

  if (!cardId) {
    return;
  }

  try {
    await apiRequest(`${USERS_API_URL}/${userId}/cards/${cardId}`, {
      method: "DELETE"
    });

    await loadFullUserProfile();
    renderProfile(currentUser);
  } catch (error) {
    profileContent.insertAdjacentHTML(
      "beforeend",
      `<p class="auth-message">${escapeHtml(error.message)}</p>`
    );
  }
}

function setFavoriteButtonState(button, isFavorite) {
  if (!button) {
    return;
  }

  button.classList.toggle("active", isFavorite);
  button.textContent = isFavorite ? "♥" : "♡";
  button.title = isFavorite ? "Remove from favorites" : "Add to favorites";
  button.setAttribute(
    "aria-label",
    isFavorite ? "Remove from favorites" : "Add to favorites"
  );
}

async function addFavoriteMovie(movieId, button) {
  const userId = getCurrentUserId();

  if (!currentUser || !userId) {
    openAuthModal();
    return;
  }

  if (!movieId) {
    return;
  }

  // Optimistic UI update: update the heart immediately.
  favoriteMovieIds.add(String(movieId));
  setFavoriteButtonState(button, true);

  try {
    await apiRequest(`${USERS_API_URL}/${userId}/favorites`, {
      method: "POST",
      body: JSON.stringify({ movieId })
    });

    // Keep currentUser.favorites locally updated without refreshing the whole movie grid.
    const movie = movies.find((item) => String(getMovieId(item)) === String(movieId));

    if (movie) {
      currentUser.favorites = currentUser.favorites || [];

      const alreadyStored = currentUser.favorites.some(
        (favorite) => String(getMovieId(favorite)) === String(movieId)
      );

      if (!alreadyStored) {
        currentUser.favorites.push(movie);
      }
    }
  } catch (error) {
    // Revert UI if backend fails.
    favoriteMovieIds.delete(String(movieId));
    setFavoriteButtonState(button, false);
    throw error;
  }
}

async function removeFavoriteMovie(movieId, button) {
  const userId = getCurrentUserId();

  if (!currentUser || !userId || !movieId) {
    return;
  }

  // Optimistic UI update: update the heart immediately.
  favoriteMovieIds.delete(String(movieId));
  setFavoriteButtonState(button, false);

  try {
    await apiRequest(`${USERS_API_URL}/${userId}/favorites/${movieId}`, {
      method: "DELETE"
    });

    // Keep currentUser.favorites locally updated without refreshing the whole movie grid.
    currentUser.favorites = (currentUser.favorites || []).filter(
      (favorite) => String(getMovieId(favorite)) !== String(movieId)
    );
  } catch (error) {
    // Revert UI if backend fails.
    favoriteMovieIds.add(String(movieId));
    setFavoriteButtonState(button, true);
    throw error;
  }
}

async function toggleFavoriteMovie(movieId, button) {
  try {
    const isAlreadyFavorite = favoriteMovieIds.has(String(movieId));

    if (isAlreadyFavorite) {
      await removeFavoriteMovie(movieId, button);
    } else {
      await addFavoriteMovie(movieId, button);
    }

    // Important:
    // Do NOT call renderMovies() here.
    // That was causing the movie cards to refresh/reanimate.
  } catch (error) {
    console.error("Favorite movie error:", error);
    alert(error.message || "Could not update favorite movie.");
  }
}

async function saveProfileChanges(event) {
  event.preventDefault();

  const form = event.target;
  const profileMessage = document.querySelector("#profileMessage");
  const submitButton = form.querySelector("button[type='submit']");
  const payload = {
    firstName: form.querySelector("#profileFirstName").value.trim(),
    lastName: form.querySelector("#profileLastName").value.trim()
  };

  if (!payload.firstName || !payload.lastName) {
    profileMessage.textContent = "First and last name are required.";
    return;
  }

  submitButton.disabled = true;
  profileMessage.textContent = "Saving profile...";

  try {
    const response = await fetch(`${USERS_API_URL}/${currentUser.userId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      throw new Error(
        getAuthErrorMessage(data, "Could not save profile changes.", response.status)
      );
    }

    currentUser = { ...currentUser, ...data };
    renderProfile(currentUser);
    profileContent.insertAdjacentHTML(
      "beforeend",
      `<p class="auth-message">Profile updated successfully.</p>`
    );
  } catch (error) {
    profileMessage.textContent = error.message;
  } finally {
    submitButton.disabled = false;
  }
}


async function openProfileModal() {
  if (!currentUser) {
    openAuthModal();
    return;
  }

  profileContent.innerHTML = `<p class="auth-message">Loading profile...</p>`;
  profileDialog.showModal();

  try {
    const user = await getCurrentUserProfile();
    renderProfile(user);
  } catch (error) {
    renderProfile(currentUser);
    profileContent.insertAdjacentHTML(
      "beforeend",
      `<p class="auth-message">${error.message}</p>`
    );
  }
}


function startProfileEdit() {
  if (!currentUser) {
    openAuthModal();
    return;
  }

  renderEditProfileForm(currentUser);
}


async function logoutCurrentUser() {
  logoutButton.disabled = true;

  try {
    const response = await fetch(`${AUTH_API_URL}/logout`, {
      method: "POST",
      credentials: "include"
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      throw new Error(
        getAuthErrorMessage(data, "Could not log out. Please try again.", response.status)
      );
    }

    currentUser = null;
    updateNavbarGreeting(null);
    updateAuthButtonForUser(null);
    profileDialog.close();
    showCustomerHome({ firstName: "CINJA member" });
  } catch (error) {
    profileContent.insertAdjacentHTML(
      "beforeend",
      `<p class="auth-message">${error.message}</p>`
    );
  } finally {
    logoutButton.disabled = false;
  }
}


/************************************************************
 * Booking prototype
 ************************************************************/
function resetBookingSelections() {
  const ticketInputs = document.querySelectorAll(
    "#adultTickets, #childTickets, #seniorTickets, .ticket-inputs input"
  );

  ticketInputs.forEach((input) => {
    input.value = 0;
    input.dispatchEvent(new Event("input", { bubbles: true }));
  });

  document.querySelectorAll(".seat.selected").forEach((seat) => {
    seat.classList.remove("selected");
  });

  const selectedSeats = document.querySelector("#selectedSeats");

  if (selectedSeats) {
    selectedSeats.textContent = "None";
  }

  updateBookingTotal();
}


function updateBookingTotal() {
  const adultTickets = Number(document.querySelector("#adultTickets")?.value) || 0;
  const childTickets = Number(document.querySelector("#childTickets")?.value) || 0;
  const seniorTickets = Number(document.querySelector("#seniorTickets")?.value) || 0;

  const total = adultTickets * 12 + childTickets * 8 + seniorTickets * 10;

  const bookingTotal = document.querySelector("#bookingTotal");

  if (bookingTotal) {
    bookingTotal.textContent = `$${total.toFixed(2)}`;
  }
}


function setupTicketQuantityControls() {
  document.addEventListener("click", (event) => {
    const button = event.target.closest("[data-ticket-action]");

    if (!button) {
      return;
    }

    const inputId = button.dataset.ticketInput;
    const action = button.dataset.ticketAction;
    const input = document.getElementById(inputId);

    if (!input) {
      return;
    }

    const currentValue = Number(input.value) || 0;

    if (action === "increase") {
      input.value = currentValue + 1;
    }

    if (action === "decrease") {
      input.value = Math.max(0, currentValue - 1);
    }

    input.dispatchEvent(new Event("input", { bubbles: true }));
    updateBookingTotal();
  });

  // Also update the total if someone manually types in the number input.
  document
    .querySelectorAll("#adultTickets, #childTickets, #seniorTickets")
    .forEach((input) => {
      input.addEventListener("input", updateBookingTotal);
    });
}


function setupSeatSelection() {
  document.addEventListener("click", (event) => {
    const seat = event.target.closest(".seat");

    if (!seat || seat.classList.contains("taken")) {
      return;
    }

    seat.classList.toggle("selected");

    const selected = [...document.querySelectorAll(".seat.selected")]
      .map((selectedSeat) => selectedSeat.textContent.trim());

    const selectedSeats = document.querySelector("#selectedSeats");

    if (selectedSeats) {
      selectedSeats.textContent =
        selected.length > 0 ? selected.join(", ") : "None";
    }
  });
}


/************************************************************
 * Click handling
 * ----------------------------------------------------------
 * One main document-level click handler handles:
 * - Movie details buttons
 * - Showtime buttons
 ************************************************************/
function handleClick(event) {
  const favoriteButton = event.target.closest("[data-favorite-movie-id]");
  const editAddressButton = event.target.closest("[data-profile-action='edit-address']");
  const addCardButton = event.target.closest("[data-profile-action='add-card']");
  const cancelProfilePanelButton = event.target.closest("[data-profile-action='cancel-profile-panel']");
  const deleteCardButton = event.target.closest("[data-delete-card-id]");
  const removeFavoriteButton = event.target.closest("[data-remove-favorite-id]");
  const detailsButton = event.target.closest("[data-details]");
  const showtimeButton = event.target.closest("[data-title][data-time]");
  const authTab = event.target.closest("[data-auth-tab]");
  const cancelProfileEdit = event.target.closest("#cancelProfileEdit");
  
  if (favoriteButton) {
  toggleFavoriteMovie(
    favoriteButton.dataset.favoriteMovieId,
    favoriteButton
  );
  return;
}

  if (editAddressButton) {
    renderAddressForm(currentUser);
    return;
  }

  if (addCardButton) {
    renderCardForm();
    return;
  }

  if (cancelProfilePanelButton) {
    renderProfile(currentUser);
    return;
  }

  if (deleteCardButton) {
    deleteCard(deleteCardButton.dataset.deleteCardId);
    return;
  }

  if (removeFavoriteButton) {
    removeFavoriteMovie(removeFavoriteButton.dataset.removeFavoriteId)
      .then(() => renderProfile(currentUser))
      .catch((error) => {
        profileContent.insertAdjacentHTML(
          "beforeend",
          `<p class="auth-message">${escapeHtml(error.message)}</p>`
        );
      });

    return;
  }

  if (cancelProfileEdit) {
    renderProfile(currentUser);
    return;
  }

  if (authTab) {
    setAuthMode(authTab.dataset.authTab);
    return;
  }

  if (detailsButton) {
    const detailsTitle = detailsButton.dataset.details;
    const movie = movies.find((item) => item.title === detailsTitle);

    if (movie) {
      openMovieDetails(movie);
    }

    return;
  }

  if (showtimeButton) {
    const showtimeTitle = showtimeButton.dataset.title;
    const showtime = showtimeButton.dataset.time;

    if (!showtime || showtime === "Coming Soon") {
      return;
    }

    const bookingSection = document.querySelector("#booking");

    if (!bookingSection) {
      console.warn("Booking section not found. Make sure it has id='booking'.");
      return;
    }

    const bookingTitle = bookingSection.querySelector("#booking-title");

    resetBookingSelections();

    if (bookingTitle) {
      bookingTitle.textContent = `${showtimeTitle} at ${showtime}`;
    }

    bookingSection.scrollIntoView({
      behavior: "smooth",
      block: "start"
    });

    bookingSection.classList.remove("booking-pulse");
    void bookingSection.offsetWidth;
    bookingSection.classList.add("booking-pulse");

    if (movieDialog && movieDialog.open) {
      movieDialog.close();
    }
  }
}


/************************************************************
 * Scroll animations
 ************************************************************/
function observeScrollAnimations() {
  const animatedElements = document.querySelectorAll(
    ".reveal, .reveal-slide-left, .reveal-slide-right, .movie-card"
  );

  if (!("IntersectionObserver" in window)) {
    animatedElements.forEach((element) => element.classList.add("is-visible"));
    return;
  }

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("is-visible");
          observer.unobserve(entry.target);
        }
      });
    },
    {
      threshold: 0.16,
      rootMargin: "0px 0px -70px 0px"
    }
  );

  animatedElements.forEach((element, index) => {
    element.style.transitionDelay = `${Math.min(index * 55, 420)}ms`;
    observer.observe(element);
  });
}


function updateScrollEffects() {
  const scrollableHeight =
    document.documentElement.scrollHeight - window.innerHeight;

  const scrollPercent =
    scrollableHeight > 0 ? (window.scrollY / scrollableHeight) * 100 : 0;

  if (scrollProgress) {
    scrollProgress.style.width = `${scrollPercent}%`;
  }

  if (siteHeader) {
    siteHeader.classList.toggle("header-scrolled", window.scrollY > 20);
  }
}


/************************************************************
 * Event listeners
 ************************************************************/
searchInput.addEventListener("input", renderMovies);
genreFilter.addEventListener("change", renderMovies);
window.addEventListener("scroll", updateScrollEffects, { passive: true });

document.addEventListener("click", handleClick);

openAuthDialog.addEventListener("click", handleAuthButtonClick);

backToCinema.addEventListener("click", () => {
  showCustomerHome(currentUser || { firstName: "CINJA member" });
});

closeAuthDialog.addEventListener("click", () => {
  authDialog.close();
});

authDialog.addEventListener("click", (event) => {
  if (event.target === authDialog) {
    authDialog.close();
  }
});

closeProfileDialog.addEventListener("click", () => {
  profileDialog.close();
});

profileDialog.addEventListener("click", (event) => {
  if (event.target === profileDialog) {
    profileDialog.close();
  }
});

logoutButton.addEventListener("click", logoutCurrentUser);
editProfileButton.addEventListener("click", startProfileEdit);

profileContent.addEventListener("submit", (event) => {
    if (event.target.id === "editProfileForm") {
      saveProfileChanges(event);
    }

    if (event.target.id === "addressForm") {
      saveAddress(event);
    }

    if (event.target.id === "cardForm") {
      saveCard(event);
    }
  });

document.querySelectorAll("[data-auth-form]").forEach((form) => {
  form.addEventListener("submit", handleAuthSubmit);
});

closeDialog.addEventListener("click", () => {
  movieDialog.close();
});

movieDialog.addEventListener("click", (event) => {
  if (event.target === movieDialog) {
    movieDialog.close();
  }
});


/************************************************************
 * App startup
 * ----------------------------------------------------------
 * Important:
 * Do NOT call renderMovies() before fetchMoviesFromBackend().
 * Otherwise, the page renders before MongoDB data arrives.
 ************************************************************/
setupTicketQuantityControls();
setupSeatSelection();
updateBookingTotal();
updateScrollEffects();
observeScrollAnimations();
fetchMoviesFromBackend();
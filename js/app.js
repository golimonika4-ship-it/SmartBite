/**
 * ==============================================================================
 * SMARTBITE – SMART CAMPUS CANTEEN MANAGEMENT SYSTEM
 * Frontend Application Logic (app.js)
 * 
 * Features:
 * 1. Category Filtering: Breakfast, Meals, Snacks, Beverages (and All Items)
 * 2. Real-time Search: by food name and description
 * 3. Dietary Filter: All, Pure Veg, Non-Veg
 * 4. Sort Options: Featured, Price (Low to High), Price (High to Low), Rating
 * 5. Dynamic Food Cards with images, names, prices in ₹, availability, & Add to Cart
 * 6. Interactive Cart: Quantity (+/-), Remove Item, Subtotal & Total calculations
 * 7. Real-time Navbar Cart Item Count badge
 * 8. Informative Toasts and Modals for previewing without pretending backend works
 * ==============================================================================
 */

// Wait for the DOM to load before initializing the application
document.addEventListener("DOMContentLoaded", () => {

  /* ============================================================================
     1. SAMPLE FOOD MENU DATA (CLIENT-SIDE SAMPLE DATA)
     ============================================================================ */
  const SAMPLE_MENU_ITEMS = [
    // --- BREAKFAST CATEGORY ---
    {
      id: "sb-b1",
      name: "Crispy Masala Dosa",
      category: "breakfast",
      categoryLabel: "Breakfast",
      isVeg: true,
      price: 50,
      availability: "Available",
      rating: 4.8,
      prepTime: "8-10 mins",
      image: "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=600&q=80",
      description: "Crispy golden fermented crepe filled with savory spiced potato masala, served with freshly ground coconut chutney and hot sambar."
    },
    {
      id: "sb-b2",
      name: "Steamed Idli & Medu Vada Combo",
      category: "breakfast",
      categoryLabel: "Breakfast",
      isVeg: true,
      price: 40,
      availability: "Available",
      rating: 4.7,
      prepTime: "5-7 mins",
      image: "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=600&q=80",
      description: "Two melt-in-mouth steamed rice idlis and one crispy golden medu vada served with piping hot dal sambar and spicy tomato chutney."
    },
    {
      id: "sb-b3",
      name: "Fluffy Poori Bhaji (3 pcs)",
      category: "breakfast",
      categoryLabel: "Breakfast",
      isVeg: true,
      price: 45,
      availability: "Available",
      rating: 4.6,
      prepTime: "8-10 mins",
      image: "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=600&q=80",
      description: "Three puffed golden whole-wheat pooris served with traditional spiced potato bhaji curry and tangy lemon pickle."
    },
    {
      id: "sb-b4",
      name: "Indori Poha with Crispy Sev",
      category: "breakfast",
      categoryLabel: "Breakfast",
      isVeg: true,
      price: 30,
      availability: "Available",
      rating: 4.9,
      prepTime: "4-5 mins",
      image: "https://images.unsplash.com/photo-1630383249896-424e482df921?auto=format&fit=crop&w=600&q=80",
      description: "Steamed flattened rice tempered with mustard seeds, crunchy peanuts, and green chillies, topped with ratlami sev and fresh coriander."
    },

    // --- MEALS CATEGORY ---
    {
      id: "sb-m1",
      name: "Paneer Butter Masala Thali",
      category: "meals",
      categoryLabel: "Meals",
      isVeg: true,
      price: 110,
      availability: "Available",
      rating: 4.9,
      prepTime: "12-15 mins",
      image: "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?auto=format&fit=crop&w=600&q=80",
      description: "Tender paneer cubes in velvety tomato cashew gravy. Served with 2 butter rotis, fragrant jeera rice, yellow dal tadka, and salad."
    },
    {
      id: "sb-m2",
      name: "Hyderabadi Chicken Dum Biryani",
      category: "meals",
      categoryLabel: "Meals",
      isVeg: false,
      price: 130,
      availability: "Available",
      rating: 4.9,
      prepTime: "10-12 mins",
      image: "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80",
      description: "Long-grain dum basmati rice slow-cooked with marinated tender chicken pieces, aromatic whole spices, cooling raita, and mirchi ka salan."
    },
    {
      id: "sb-m3",
      name: "Special Deluxe Veg Mini Thali",
      category: "meals",
      categoryLabel: "Meals",
      isVeg: true,
      price: 85,
      availability: "Available",
      rating: 4.7,
      prepTime: "10-12 mins",
      image: "https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?auto=format&fit=crop&w=600&q=80",
      description: "Balanced campus lunch thali with seasonal paneer sabzi, dal fry, 2 soft phulkas, steamed basmati rice, papad, and mixed pickle."
    },
    {
      id: "sb-m4",
      name: "Homestyle Rajma Chawal Bowl",
      category: "meals",
      categoryLabel: "Meals",
      isVeg: true,
      price: 70,
      availability: "Available",
      rating: 4.8,
      prepTime: "8-10 mins",
      image: "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=600&q=80",
      description: "Hearty North-Indian red kidney beans slow-simmered in rich spiced onion-tomato gravy, served over steaming hot basmati rice with sliced onions."
    },

    // --- SNACKS CATEGORY ---
    {
      id: "sb-s1",
      name: "Veg Cheese Grilled Sandwich",
      category: "snacks",
      categoryLabel: "Snacks",
      isVeg: true,
      price: 60,
      availability: "Available",
      rating: 4.6,
      prepTime: "6-8 mins",
      image: "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?auto=format&fit=crop&w=600&q=80",
      description: "Toasted 3-layer sandwich stuffed with sweet corn, bell peppers, fresh mint chutney, mozzarella cheese, and secret sandwich herbs."
    },
    {
      id: "sb-s2",
      name: "Crispy Herb Veg Burger",
      category: "snacks",
      categoryLabel: "Snacks",
      isVeg: true,
      price: 75,
      availability: "Available",
      rating: 4.5,
      prepTime: "8-10 mins",
      image: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=600&q=80",
      description: "Golden crumbed vegetable patty loaded with peri-peri mayo, crunchy lettuce, sliced tomato, and cheddar cheese inside a sesame bun."
    },
    {
      id: "sb-s3",
      name: "Delhi Samosa Chaat (2 pcs)",
      category: "snacks",
      categoryLabel: "Snacks",
      isVeg: true,
      price: 40,
      availability: "Available",
      rating: 4.8,
      prepTime: "5 mins",
      image: "https://images.unsplash.com/photo-1541544741938-0af808871cc0?auto=format&fit=crop&w=600&q=80",
      description: "Crispy spiced potato samosas crushed and smothered with warm Punjabi chole, sweetened yogurt, tangy tamarind chutney, and nylon sev."
    },
    {
      id: "sb-s4",
      name: "Peri-Peri Crispy French Fries",
      category: "snacks",
      categoryLabel: "Snacks",
      isVeg: true,
      price: 50,
      availability: "Available",
      rating: 4.6,
      prepTime: "6 mins",
      image: "https://images.unsplash.com/photo-1576107232684-1279f3908594?auto=format&fit=crop&w=600&q=80",
      description: "Golden crunchy potato fries tossed in zesty peri-peri spice dust, served hot with creamy garlic dip and tomato ketchup."
    },

    // --- BEVERAGES CATEGORY ---
    {
      id: "sb-v1",
      name: "Thick Cold Coffee with Ice Cream",
      category: "beverages",
      categoryLabel: "Beverages",
      isVeg: true,
      price: 55,
      availability: "Available",
      rating: 4.9,
      prepTime: "4 mins",
      image: "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?auto=format&fit=crop&w=600&q=80",
      description: "Creamy blended cold brew coffee topped with a velvety scoop of vanilla ice cream and rich chocolate syrup drizzle."
    },
    {
      id: "sb-v2",
      name: "Special Masala Cutting Chai",
      category: "beverages",
      categoryLabel: "Beverages",
      isVeg: true,
      price: 20,
      availability: "Available",
      rating: 4.9,
      prepTime: "3 mins",
      image: "https://images.unsplash.com/photo-1576092768241-dec231879fc3?auto=format&fit=crop&w=600&q=80",
      description: "Campus favorite freshly boiled Indian tea infused with crushed ginger, cardamom, and clove, served piping hot in an authentic clay kulhad."
    },
    {
      id: "sb-v3",
      name: "Fresh Lemon Mint Mojito Cooler",
      category: "beverages",
      categoryLabel: "Beverages",
      isVeg: true,
      price: 35,
      availability: "Available",
      rating: 4.5,
      prepTime: "3 mins",
      image: "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=600&q=80",
      description: "Sparkling soda cooler infused with hand-crushed fresh mint sprigs, tangy lime juice, and a pinch of black salt for instant hydration."
    },
    {
      id: "sb-v4",
      name: "Traditional Mango Lassi",
      category: "beverages",
      categoryLabel: "Beverages",
      isVeg: true,
      price: 45,
      availability: "Available",
      rating: 4.8,
      prepTime: "4 mins",
      image: "https://images.unsplash.com/photo-1546173159-315724a31696?auto=format&fit=crop&w=600&q=80",
      description: "Thick sweet yogurt smoothie churned with pure Alphonso mango pulp, fragrant cardamom powder, and garnished with slivered almonds."
    }
  ];

  /* Exact Categories Requested: Breakfast, Meals, Snacks, Beverages (plus All Items) */
  const CATEGORIES = [
    { id: "all", name: "All Items", icon: "fa-solid fa-border-all" },
    { id: "breakfast", name: "Breakfast", icon: "fa-solid fa-mug-saucer" },
    { id: "meals", name: "Meals", icon: "fa-solid fa-bowl-food" },
    { id: "snacks", name: "Snacks", icon: "fa-solid fa-cookie-bite" },
    { id: "beverages", name: "Beverages", icon: "fa-solid fa-glass-water" }
  ];

  /* Fallback placeholder SVG image in case the device is offline */
  const FALLBACK_IMAGE_DATA = "data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='600' height='400' viewBox='0 0 600 400'%3E%3Crect fill='%23FDF8F0' width='600' height='400'/%3E%3Ctext fill='%23EA580C' font-family='sans-serif' font-size='26' font-weight='bold' x='50%25' y='50%25' text-anchor='middle'%3ESmartBite Campus Food%3C/text%3E%3C/svg%3E";

  /* Configurable API Base URL - defaults to http://localhost:8080/api */
  const API_BASE_URL = window.SMARTBITE_API_BASE_URL || "http://localhost:8080/api";

  /* ============================================================================
     2. APPLICATION REACTIVE STATE
     ============================================================================ */
  const state = {
    searchQuery: "",
    selectedCategory: "all",
    selectedDiet: "all", // 'all', 'veg', 'non-veg'
    sortBy: "featured",   // 'featured', 'price-low', 'price-high', 'rating'
    cart: [],             // Working cart items: [{ id, name, price, image, quantity, categoryLabel }]
    menuItems: [],        // Dynamically fetched from MySQL via GET /api/menu
    isLive: false,        // True when connected to Spring Boot MySQL backend
    isSubmittingOrder: false, // Guard to prevent duplicate checkout submissions
    currentCustomerId: localStorage.getItem("smartbite_customer_id") || "21CS101", // Demo Student ID
    activeTable: null,    // Selected table e.g. "T01" when scanned from QR
    demoMode: true,       // Configurable demo mode
    configuredTables: ["T01", "T02", "T03", "T04", "T05", "T06", "T07", "T08", "T09", "T10"],
    qrBaseUrl: localStorage.getItem("smartbite_qr_base_url") || (window.location.origin + window.location.pathname),
    activeTrackingToken: localStorage.getItem("smartbite_active_tracking_token") || null,
    customerPollingTimer: null,
    lastPolledStatus: null
  };

  /* ============================================================================
     3. DOM ELEMENT REFERENCES
     ============================================================================ */
  const foodGrid = document.getElementById("foodGrid");
  const emptyState = document.getElementById("emptyState");
  const emptyResetBtn = document.getElementById("emptyResetBtn");
  const categoriesBar = document.getElementById("categoriesBar");
  const foodSearchInput = document.getElementById("foodSearchInput");
  const clearSearchBtn = document.getElementById("clearSearchBtn");
  const dietaryBtns = document.querySelectorAll(".dietary-btn");
  const sortSelect = document.getElementById("sortSelect");
  const itemCountDisplay = document.getElementById("itemCountDisplay");
  const resetFiltersBtn = document.getElementById("resetFiltersBtn");

  // Navbar and Cart Trigger
  const mobileMenuToggle = document.getElementById("mobileMenuToggle");
  const navMenu = document.getElementById("navMenu");
  const cartTriggerBtn = document.getElementById("cartTriggerBtn");
  const cartCountBadge = document.getElementById("cartCountBadge");

  // Cart Drawer
  const trayDrawer = document.getElementById("trayDrawer");
  const trayBackdrop = document.getElementById("trayBackdrop");
  const closeTrayBtn = document.getElementById("closeTrayBtn");
  const trayItemsContainer = document.getElementById("trayItemsContainer");
  const traySubtotal = document.getElementById("traySubtotal");
  const trayGrandTotal = document.getElementById("trayGrandTotal");
  const demoCheckoutBtn = document.getElementById("demoCheckoutBtn");
  const clearTrayBtn = document.getElementById("clearTrayBtn");

  // Modals & Navigation triggers
  const loginModalBtn = document.getElementById("loginModalBtn");
  const loginModal = document.getElementById("loginModal");
  const adminModalBtn = document.getElementById("adminModalBtn");
  const adminModal = document.getElementById("adminModal");
  const navOrdersBtn = document.getElementById("navOrdersBtn");
  const ordersModal = document.getElementById("ordersModal");
  const heroTrackBtn = document.getElementById("heroTrackBtn");
  const toastContainer = document.getElementById("toastContainer");

  // Phase 3: Auth Elements & Reactive Auth State
  const userNavChip = document.getElementById("userNavChip");
  const userNavName = document.getElementById("userNavName");
  const userNavRole = document.getElementById("userNavRole");
  const logoutBtn = document.getElementById("logoutBtn");
  const mobileAuthNavGroup = document.getElementById("mobileAuthNavGroup");
  const mobileUserNavGroup = document.getElementById("mobileUserNavGroup");
  const mobileUserName = document.getElementById("mobileUserName");
  const mobileUserRole = document.getElementById("mobileUserRole");
  const mobileLogoutBtn = document.getElementById("mobileLogoutBtn");

  const tabStudentLoginBtn = document.getElementById("tabStudentLoginBtn");
  const tabStudentRegisterBtn = document.getElementById("tabStudentRegisterBtn");
  const tabAdminLoginBtn = document.getElementById("tabAdminLoginBtn");
  const studentLoginForm = document.getElementById("studentLoginForm");
  const studentRegisterForm = document.getElementById("studentRegisterForm");
  const adminLoginForm = document.getElementById("adminLoginForm");
  const authAlertBox = document.getElementById("authAlertBox");
  const loginModalTitle = document.getElementById("loginModalTitle");
  const loginModalSubtitle = document.getElementById("loginModalSubtitle");
  const authModalIcon = document.getElementById("authModalIcon");
  const switchToRegisterLink = document.getElementById("switchToRegisterLink");
  const switchToLoginLink = document.getElementById("switchToLoginLink");

  const storedUserRaw = localStorage.getItem("smartbite_auth_user");
  let initialStoredUser = null;
  try {
    if (storedUserRaw) initialStoredUser = JSON.parse(storedUserRaw);
  } catch (_) {}

  const initialToken = sessionStorage.getItem("smartbite_token") || localStorage.getItem("smartbite_token") || null;

  const authState = {
    token: initialToken,
    user: initialStoredUser, // { id, name, collegeRollNumber, email, role }
    role: initialStoredUser?.role || null, // 'STUDENT' or 'ADMIN'
    isLoggedIn: Boolean(initialToken)
  };

  function getAuthHeaders(customHeaders = {}) {
    const headers = { ...customHeaders };
    if (authState.token) {
      headers["Authorization"] = `Bearer ${authState.token}`;
    }
    return headers;
  }

  /* ============================================================================
     4. CATEGORY TABS RENDERING
     ============================================================================ */
  function renderCategoryPills() {
    categoriesBar.innerHTML = CATEGORIES.map(cat => `
      <button 
        type="button" 
        class="cat-pill ${cat.id === state.selectedCategory ? 'active' : ''}" 
        data-category="${cat.id}"
        role="tab"
        aria-selected="${cat.id === state.selectedCategory}"
      >
        <i class="${cat.icon}"></i>
        <span>${cat.name}</span>
      </button>
    `).join("");

    // Attach click listeners to category buttons
    categoriesBar.querySelectorAll(".cat-pill").forEach(pill => {
      pill.addEventListener("click", () => {
        state.selectedCategory = pill.getAttribute("data-category");
        renderCategoryPills();
        filterAndRenderMenu();
      });
    });
  }

  /* ============================================================================
     5. MENU FILTERING, SEARCHING & FOOD CARD RENDERING
     ============================================================================ */
  function filterAndRenderMenu() {
    const sourceItems = (state.menuItems && state.menuItems.length > 0) ? state.menuItems : SAMPLE_MENU_ITEMS;
    let items = [...sourceItems];

    // 1. Filter by category (Breakfast, Meals, Snacks, Beverages)
    if (state.selectedCategory !== "all") {
      items = items.filter(item => item.category === state.selectedCategory);
    }

    // 2. Filter by dietary choice (Pure Veg vs Non-Veg)
    if (state.selectedDiet === "veg") {
      items = items.filter(item => item.isVeg === true);
    } else if (state.selectedDiet === "non-veg") {
      items = items.filter(item => item.isVeg === false);
    }

    // 3. Filter by search query
    if (state.searchQuery.trim() !== "") {
      const q = state.searchQuery.toLowerCase().trim();
      items = items.filter(item => 
        item.name.toLowerCase().includes(q) || 
        item.description.toLowerCase().includes(q) ||
        item.categoryLabel.toLowerCase().includes(q)
      );
    }

    // 4. Sort results
    if (state.sortBy === "price-low") {
      items.sort((a, b) => a.price - b.price);
    } else if (state.sortBy === "price-high") {
      items.sort((a, b) => b.price - a.price);
    } else if (state.sortBy === "rating") {
      items.sort((a, b) => b.rating - a.rating);
    } // 'featured' keeps curated order

    // Update count display
    if (itemCountDisplay) {
      itemCountDisplay.textContent = items.length;
    }

    // Render results or show empty state
    if (items.length === 0) {
      foodGrid.innerHTML = "";
      emptyState.classList.remove("hidden");
    } else {
      emptyState.classList.add("hidden");
      renderFoodCards(items);
    }
  }

  function renderFoodCards(items) {
    foodGrid.innerHTML = items.map(item => {
      const isOutOfStock = Boolean(item.availability && item.availability.toLowerCase().includes("out of stock"));
      return `
      <article class="food-card ${isOutOfStock ? 'out-of-stock-card' : ''}" data-id="${item.id}">
        <!-- Top Media Section: Image with Badges -->
        <div class="food-card-img-wrap">
          <img 
            src="${item.image}" 
            alt="${item.name}" 
            class="food-card-img" 
            loading="lazy"
            onerror="this.onerror=null; this.src='${FALLBACK_IMAGE_DATA}';"
          />

          <!-- Availability Badge -->
          <span class="availability-badge ${isOutOfStock ? 'out-of-stock' : 'in-stock'}">
            <i class="fa-solid ${isOutOfStock ? 'fa-ban' : 'fa-circle-check'}"></i> ${isOutOfStock ? 'Out of Stock' : (item.availability || 'Available')}
          </span>

          <!-- Diet Dot Badge -->
          <span 
            class="diet-indicator ${item.isVeg ? 'veg' : 'non-veg'}" 
            title="${item.isVeg ? 'Pure Vegetarian' : 'Non-Vegetarian'}"
            aria-label="${item.isVeg ? 'Pure Vegetarian' : 'Non-Vegetarian'}"
          ></span>

          <!-- Prep Time Tag -->
          <span class="card-prep-tag">
            <i class="fa-solid fa-stopwatch"></i> ${item.prepTime}
          </span>
        </div>

        <!-- Food Information Body -->
        <div class="card-content">
          <div class="card-meta-row">
            <span class="card-category-badge">${item.categoryLabel}</span>
            <span class="card-rating-tag"><i class="fa-solid fa-star"></i> ${item.rating.toFixed(1)}</span>
          </div>

          <h3 class="food-title">${item.name}</h3>
          <p class="food-desc">${item.description}</p>

          <!-- Price & Add to Cart Button -->
          <div class="card-footer">
            <div class="food-price-wrap">
              <span class="price-label">Campus Price</span>
              <span class="food-price">₹${item.price}</span>
            </div>
            ${isOutOfStock ? `
              <button 
                type="button" 
                class="btn-add-food btn-sold-out" 
                disabled
                aria-label="${item.name} is Out of Stock"
              >
                <i class="fa-solid fa-ban"></i> Out of Stock
              </button>
            ` : `
              <button 
                type="button" 
                class="btn-add-food" 
                data-add-id="${item.id}"
                aria-label="Add ${item.name} to Cart"
              >
                <i class="fa-solid fa-cart-plus"></i> Add to Cart
              </button>
            `}
          </div>
        </div>
      </article>
      `;
    }).join("");

    // Attach click listeners to all Add to Cart buttons
    foodGrid.querySelectorAll("[data-add-id]").forEach(btn => {
      btn.addEventListener("click", () => {
        const itemId = btn.getAttribute("data-add-id");
        addToCart(itemId);
      });
    });
  }

  /* ============================================================================
     6. WORKING CART LOGIC (QUANTITY, REMOVE ITEM, TOTAL CALCULATION)
     ============================================================================ */
  
  /**
   * Add item to cart or increment quantity if already present
   */
  function addToCart(itemId) {
    const sourceItems = (state.menuItems && state.menuItems.length > 0) ? state.menuItems : SAMPLE_MENU_ITEMS;
    const foodItem = sourceItems.find(item => item.id === itemId);
    if (!foodItem) return;

    if (foodItem.availability && foodItem.availability.toLowerCase().includes("out of stock")) {
      showToast("Item Unavailable", `"${foodItem.name}" is currently out of stock.`, "fa-solid fa-ban");
      return;
    }

    const existingIndex = state.cart.findIndex(c => c.id === itemId);
    if (existingIndex > -1) {
      state.cart[existingIndex].quantity += 1;
    } else {
      state.cart.push({
        id: foodItem.id,
        name: foodItem.name,
        price: foodItem.price,
        image: foodItem.image,
        categoryLabel: foodItem.categoryLabel,
        quantity: 1
      });
    }

    updateCartUI();
    showToast(
      "Item Added to Cart",
      `"${foodItem.name}" was added to your cart. Total items: ${getCartTotalCount()}`,
      "fa-solid fa-cart-shopping"
    );
  }

  /**
   * Increase or decrease quantity of an item
   */
  function changeQuantity(itemId, delta) {
    const itemIndex = state.cart.findIndex(c => c.id === itemId);
    if (itemIndex === -1) return;

    state.cart[itemIndex].quantity += delta;
    if (state.cart[itemIndex].quantity <= 0) {
      removeFromCart(itemId);
      return;
    }

    updateCartUI();
  }

  /**
   * Remove item completely from cart
   */
  function removeFromCart(itemId) {
    const itemIndex = state.cart.findIndex(c => c.id === itemId);
    if (itemIndex === -1) return;

    const removedItemName = state.cart[itemIndex].name;
    state.cart.splice(itemIndex, 1);
    updateCartUI();

    showToast(
      "Item Removed",
      `"${removedItemName}" was removed from your cart.`,
      "fa-solid fa-trash-can"
    );
  }

  /**
   * Clear all items from cart
   */
  function clearCart() {
    if (state.cart.length === 0) return;
    state.cart = [];
    updateCartUI();
    showToast("Cart Emptied", "All items have been cleared from your cart.", "fa-solid fa-trash-can");
  }

  /**
   * Calculate total count of items in cart
   */
  function getCartTotalCount() {
    return state.cart.reduce((sum, item) => sum + item.quantity, 0);
  }

  /**
   * Calculate subtotal price in INR
   */
  function getCartSubtotal() {
    return state.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  }

  /**
   * Refresh the Cart UI: items list, totals, and navbar badge count
   */
  function updateCartUI() {
    const totalCount = getCartTotalCount();
    const subtotal = getCartSubtotal();

    // 1. Update Navbar Cart Count Badge
    if (cartCountBadge) {
      cartCountBadge.textContent = totalCount;
      if (totalCount > 0) {
        cartCountBadge.classList.add("has-items");
      } else {
        cartCountBadge.classList.remove("has-items");
      }
    }

    // 2. Update Cart Drawer Totals
    if (traySubtotal) traySubtotal.textContent = `₹${subtotal}`;
    if (trayGrandTotal) trayGrandTotal.textContent = `₹${subtotal}`;

    // 3. Render Cart Drawer Items List
    if (!trayItemsContainer) return;

    if (state.cart.length === 0) {
      trayItemsContainer.innerHTML = `
        <div class="tray-empty">
          <i class="fa-solid fa-cart-arrow-down"></i>
          <h4>Your cart is empty</h4>
          <p>Explore the menu and add your favorite breakfast, meals, snacks, or drinks!</p>
        </div>
      `;
    } else {
      trayItemsContainer.innerHTML = state.cart.map(item => `
        <div class="tray-item-row" data-cart-id="${item.id}">
          <!-- Food Thumbnail -->
          <img 
            src="${item.image}" 
            alt="${item.name}" 
            class="tray-item-thumb" 
            onerror="this.onerror=null; this.src='${FALLBACK_IMAGE_DATA}';"
          />

          <!-- Food Details -->
          <div class="tray-item-info">
            <h4 class="tray-item-name">${item.name}</h4>
            <span class="tray-item-unit-price">₹${item.price} each</span>
          </div>

          <!-- Quantity Controls -->
          <div class="tray-qty-controls">
            <button 
              type="button" 
              class="qty-btn" 
              data-action="decrease" 
              data-id="${item.id}" 
              title="Decrease quantity" 
              aria-label="Decrease quantity of ${item.name}"
            >
              <i class="fa-solid fa-minus"></i>
            </button>
            <span class="qty-value">${item.quantity}</span>
            <button 
              type="button" 
              class="qty-btn" 
              data-action="increase" 
              data-id="${item.id}" 
              title="Increase quantity" 
              aria-label="Increase quantity of ${item.name}"
            >
              <i class="fa-solid fa-plus"></i>
            </button>
          </div>

          <!-- Item Subtotal Price -->
          <div class="tray-item-total">
            ₹${item.price * item.quantity}
          </div>

          <!-- Remove Item Button -->
          <button 
            type="button" 
            class="btn-remove-item" 
            data-action="remove" 
            data-id="${item.id}" 
            title="Remove ${item.name} from cart"
            aria-label="Remove ${item.name} from cart"
          >
            <i class="fa-solid fa-trash-can"></i>
          </button>
        </div>
      `).join("");

      // Attach event listeners for Quantity + / - buttons
      trayItemsContainer.querySelectorAll("[data-action='increase']").forEach(btn => {
        btn.addEventListener("click", () => {
          changeQuantity(btn.getAttribute("data-id"), 1);
        });
      });

      trayItemsContainer.querySelectorAll("[data-action='decrease']").forEach(btn => {
        btn.addEventListener("click", () => {
          changeQuantity(btn.getAttribute("data-id"), -1);
        });
      });

      // Attach event listeners for Remove Item buttons
      trayItemsContainer.querySelectorAll("[data-action='remove']").forEach(btn => {
        btn.addEventListener("click", () => {
          removeFromCart(btn.getAttribute("data-id"));
        });
      });
    }
  }

  /* Cart Drawer Open / Close */
  function openCartDrawer() {
    if (!trayDrawer) return;
    trayDrawer.classList.add("open");
    trayBackdrop.classList.add("open");
    trayDrawer.setAttribute("aria-hidden", "false");
    document.body.style.overflow = "hidden";
  }

  function closeCartDrawer() {
    if (!trayDrawer) return;
    trayDrawer.classList.remove("open");
    trayBackdrop.classList.remove("open");
    trayDrawer.setAttribute("aria-hidden", "true");
    document.body.style.overflow = "";
  }

  /* ============================================================================
     7. MODAL CONTROLS (LOGIN, ADMIN, ORDERS)
     ============================================================================ */
  function openModal(modalEl) {
    if (!modalEl) return;
    modalEl.classList.add("open");
    modalEl.setAttribute("aria-hidden", "false");
    document.body.style.overflow = "hidden";
  }

  function closeModal(modalEl) {
    if (!modalEl) return;
    modalEl.classList.remove("open");
    modalEl.setAttribute("aria-hidden", "true");
    document.body.style.overflow = "";
    if (modalEl === adminModal) {
      stopAdminAutoRefresh();
    }
  }

  // Bind close buttons with data-close attribute
  document.querySelectorAll("[data-close]").forEach(btn => {
    btn.addEventListener("click", () => {
      const modalId = btn.getAttribute("data-close");
      const targetModal = document.getElementById(modalId);
      closeModal(targetModal);
    });
  });

  // Close modals on clicking outside overlay
  document.querySelectorAll(".modal-backdrop").forEach(backdrop => {
    backdrop.addEventListener("click", (e) => {
      if (e.target === backdrop) closeModal(backdrop);
    });
  });

  /* ============================================================================
     8. TOAST NOTIFICATION SYSTEM
     ============================================================================ */
  function showToast(title, message, iconClass = "fa-solid fa-circle-info") {
    if (!toastContainer) return;

    const toast = document.createElement("div");
    toast.className = "toast";
    toast.innerHTML = `
      <i class="${iconClass}"></i>
      <div class="toast-content">
        <div class="toast-title">${title}</div>
        <div class="toast-msg">${message}</div>
      </div>
    `;

    toastContainer.appendChild(toast);

    // Auto dismiss after 3.5 seconds
    setTimeout(() => {
      toast.classList.add("toast-leave");
      setTimeout(() => toast.remove(), 300);
    }, 3500);
  }

  /* ============================================================================
     9. EVENT LISTENERS SETUP
     ============================================================================ */

  // Live Food Search Input
  if (foodSearchInput) {
    foodSearchInput.addEventListener("input", (e) => {
      state.searchQuery = e.target.value;
      if (clearSearchBtn) {
        clearSearchBtn.style.display = state.searchQuery.length > 0 ? "block" : "none";
      }
      filterAndRenderMenu();
    });
  }

  // Clear Search button
  if (clearSearchBtn) {
    clearSearchBtn.addEventListener("click", () => {
      foodSearchInput.value = "";
      state.searchQuery = "";
      clearSearchBtn.style.display = "none";
      foodSearchInput.focus();
      filterAndRenderMenu();
    });
  }

  // Dietary preference toggle (All / Pure Veg / Non-Veg)
  dietaryBtns.forEach(btn => {
    btn.addEventListener("click", () => {
      dietaryBtns.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      state.selectedDiet = btn.getAttribute("data-diet");
      filterAndRenderMenu();
    });
  });

  // Sort dropdown change
  if (sortSelect) {
    sortSelect.addEventListener("change", (e) => {
      state.sortBy = e.target.value;
      filterAndRenderMenu();
    });
  }

  // Reset Filters Function
  function resetAllFilters() {
    state.searchQuery = "";
    state.selectedCategory = "all";
    state.selectedDiet = "all";
    state.sortBy = "featured";

    if (foodSearchInput) foodSearchInput.value = "";
    if (clearSearchBtn) clearSearchBtn.style.display = "none";
    if (sortSelect) sortSelect.value = "featured";

    dietaryBtns.forEach(b => {
      b.classList.toggle("active", b.getAttribute("data-diet") === "all");
    });

    renderCategoryPills();
    filterAndRenderMenu();
  }

  if (resetFiltersBtn) resetFiltersBtn.addEventListener("click", resetAllFilters);
  if (emptyResetBtn) emptyResetBtn.addEventListener("click", resetAllFilters);

  // Cart Drawer Triggers
  if (cartTriggerBtn) cartTriggerBtn.addEventListener("click", openCartDrawer);
  if (closeTrayBtn) closeTrayBtn.addEventListener("click", closeCartDrawer);
  if (trayBackdrop) trayBackdrop.addEventListener("click", closeCartDrawer);
  if (clearTrayBtn) clearTrayBtn.addEventListener("click", clearCart);

  // Real Canteen Order Placement via POST /api/orders
  if (demoCheckoutBtn) {
    demoCheckoutBtn.addEventListener("click", async () => {
      // 1. Guard against empty cart
      if (state.cart.length === 0) {
        showToast("Cart is Empty", "Add some delicious food to your cart first!", "fa-solid fa-triangle-exclamation");
        return;
      }

      // 2. Guard: Must be authenticated student UNLESS in table mode or demo mode
      if (!authState.isLoggedIn && !state.activeTable && !state.demoMode) {
        showToast(
          "Student Sign In Required",
          "Please sign in or register with your college roll number before placing an order.",
          "fa-solid fa-lock"
        );
        closeCartDrawer();
        openModal(loginModal);
        switchAuthTab("login");
        return;
      }

      // 3. Prevent accidental duplicate submissions
      if (state.isSubmittingOrder) return;

      // 4. Prepare payload: send ONLY item IDs and quantities, NEVER client prices
      const rawNameInput = document.getElementById("trayCustomerNameInput");
      const guestName = rawNameInput ? rawNameInput.value.trim() : "";
      let identifier = authState.user?.collegeRollNumber || guestName;
      if (!identifier) {
        identifier = state.activeTable ? `Table ${state.activeTable}` : (state.currentCustomerId || "Guest");
      }

      const orderPayload = {
        tableId: state.activeTable || null,
        customerIdentifier: identifier,
        items: state.cart.map(item => ({
          itemId: item.id,
          quantity: item.quantity
        }))
      };

      state.isSubmittingOrder = true;
      demoCheckoutBtn.disabled = true;
      const originalBtnHtml = demoCheckoutBtn.innerHTML;
      demoCheckoutBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Placing Order...`;

      try {
        const response = await fetch(`${API_BASE_URL}/orders`, {
          method: "POST",
          credentials: "include",
          headers: getAuthHeaders({
            "Content-Type": "application/json",
            "Accept": "application/json"
          }),
          body: JSON.stringify(orderPayload)
        });

        if (!response.ok) {
          const errData = await response.json().catch(() => null);
          const errMsg = (errData && errData.message) ? errData.message : `Server error (HTTP ${response.status})`;
          throw new Error(errMsg);
        }

        const orderData = await response.json();

        // 4. Clear cart ONLY after verified server confirmation
        state.cart = [];
        updateCartUI();
        closeCartDrawer();

        // 5. Populate and display the Order Confirmation Modal
        const orderSuccessModal = document.getElementById("orderSuccessModal");
        const tokenEl = document.getElementById("successPickupToken");
        const idEl = document.getElementById("successOrderId");
        const statusEl = document.getElementById("successOrderStatus");
        const timeEl = document.getElementById("successOrderTime");
        const totalEl = document.getElementById("successOrderTotal");
        const itemsEl = document.getElementById("successOrderItems");
        const tableTagEl = document.getElementById("successOrderTableTag");

        if (tokenEl) tokenEl.textContent = `#${orderData.pickupToken}`;
        if (idEl) idEl.textContent = `#${orderData.id}`;
        if (statusEl) statusEl.textContent = orderData.status;
        if (timeEl) timeEl.textContent = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        if (totalEl) totalEl.textContent = `₹${orderData.grandTotal.toFixed(2)}`;

        const assignedTable = orderData.tableId || state.activeTable;
        if (tableTagEl) {
          if (assignedTable) {
            tableTagEl.innerHTML = `<i class="fa-solid fa-chair"></i> Table ${escapeHtml(assignedTable)}`;
            tableTagEl.style.display = "inline-flex";
          } else {
            tableTagEl.style.display = "none";
          }
        }

        if (itemsEl && Array.isArray(orderData.items)) {
          itemsEl.innerHTML = orderData.items.map(item => `
            <div class="order-summary-row">
              <span>${item.quantity}x ${item.itemName}</span>
              <strong>₹${item.lineTotal.toFixed(2)}</strong>
            </div>
          `).join("");
        }

        // Reset stepper for fresh pending status
        updateCustomerOrderStepperUI(orderData);

        if (orderSuccessModal) {
          openModal(orderSuccessModal);
        }

        // 6. Start real-time status tracking for this token
        startCustomerOrderTracking(orderData.pickupToken);

        showToast(
          "Order Confirmed!",
          `Pickup Token #${orderData.pickupToken} assigned. Total: ₹${orderData.grandTotal.toFixed(2)}`,
          "fa-solid fa-circle-check"
        );

      } catch (err) {
        console.error("[SmartBite] Order creation failed:", err);
        showToast(
          "Order Placement Failed",
          err.message || "Could not connect to backend server. Your cart items are preserved.",
          "fa-solid fa-triangle-exclamation"
        );
      } finally {
        state.isSubmittingOrder = false;
        demoCheckoutBtn.disabled = false;
        demoCheckoutBtn.innerHTML = originalBtnHtml;
      }
    });
  }

  // Helper: Escape HTML to prevent XSS in dynamic rendering
  function escapeHtml(str) {
    if (!str) return "";
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

  // Live Customer Orders Loading & Rendering
  async function loadCustomerOrders() {
    const ordersListContainer = document.getElementById("ordersListContainer");
    const ordersStudentIdInput = document.getElementById("ordersStudentIdInput");
    const refreshBtn = document.getElementById("refreshOrdersBtn");
    const ordersAuthUserLabel = document.getElementById("ordersAuthUserLabel");
    const ordersAuthStatusTitle = document.getElementById("ordersAuthStatusTitle");
    if (!ordersListContainer) return;

    if (authState.isLoggedIn) {
      if (ordersAuthStatusTitle) ordersAuthStatusTitle.textContent = "Private Order History (Verified)";
      if (ordersAuthUserLabel) {
        ordersAuthUserLabel.innerHTML = `Signed in as <strong>${escapeHtml(authState.user?.name || "Student")}</strong> (${escapeHtml(authState.user?.collegeRollNumber || "")}) • Orders scoped to your account`;
      }
    } else {
      if (ordersAuthStatusTitle) ordersAuthStatusTitle.textContent = "Guest / Unauthenticated";
      if (ordersAuthUserLabel) {
        ordersAuthUserLabel.innerHTML = `Please sign in to view your orders and live pickup tokens.`;
      }
    }

    if (!authState.isLoggedIn) {
      ordersListContainer.innerHTML = `
        <div class="orders-empty-state">
          <i class="fa-solid fa-user-lock"></i>
          <h4>Student Sign In Required</h4>
          <p>Please sign in to view your private canteen orders and live kitchen pickup tokens.</p>
          <button class="btn btn-primary btn-sm mt-3" id="openLoginFromOrdersBtn" type="button">
            <i class="fa-solid fa-right-to-bracket"></i> Sign In to Account
          </button>
        </div>
      `;
      const promptBtn = document.getElementById("openLoginFromOrdersBtn");
      if (promptBtn) {
        promptBtn.addEventListener("click", () => {
          closeModal(ordersModal);
          openModal(loginModal);
          switchAuthTab("login");
        });
      }
      return;
    }

    // Show loading state
    ordersListContainer.innerHTML = `
      <div class="orders-loading-state">
        <i class="fa-solid fa-spinner fa-spin"></i>
        <span>Loading orders for <strong>${escapeHtml(authState.user?.name || "Student")}</strong>...</span>
      </div>
    `;

    if (refreshBtn) {
      refreshBtn.disabled = true;
      refreshBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Refreshing...`;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/orders/my`, {
        method: "GET",
        credentials: "include",
        headers: getAuthHeaders({
          "Accept": "application/json"
        })
      });

      if (response.status === 401 || response.status === 403) {
        clearAuthState();
        updateAuthUI();
        throw new Error("Session expired or unauthorized. Please sign in again.");
      }

      if (!response.ok) {
        throw new Error(`Server returned HTTP ${response.status}`);
      }

      const orders = await response.json();

      if (!Array.isArray(orders) || orders.length === 0) {
        ordersListContainer.innerHTML = `
          <div class="orders-empty-state">
            <i class="fa-solid fa-receipt"></i>
            <h4>No Orders Found</h4>
            <p>You haven't placed any canteen orders yet. Check out the menu and place an order!</p>
          </div>
        `;
        return;
      }

      ordersListContainer.innerHTML = orders.map(order => {
        const statusClass = `status-${(order.status || "pending").toLowerCase()}`;
        const formattedDate = order.createdAt 
          ? new Date(order.createdAt).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' })
          : "Recently";

        const itemsSummary = (order.items && order.items.length > 0)
          ? order.items.map(it => `<span>${it.quantity}x ${escapeHtml(it.itemName)} (₹${Number(it.unitPrice).toFixed(2)})</span>`).join(", ")
          : "Item details unavailable";

        return `
          <div class="order-history-card">
            <div class="order-hist-header">
              <div>
                <span class="token-pill">Token #${escapeHtml(order.pickupToken)}</span>
                <span class="order-date"><i class="fa-solid fa-clock"></i> ${formattedDate}</span>
              </div>
              <span class="status-pill ${statusClass}">${escapeHtml(order.status)}</span>
            </div>
            <div class="order-hist-items">
              ${itemsSummary}
            </div>
            <div class="order-hist-footer">
              <span>Order #${order.id} • ${escapeHtml(order.customerIdentifier)}</span>
              <strong>₹${Number(order.grandTotal).toFixed(2)}</strong>
            </div>
          </div>
        `;
      }).join("");

    } catch (err) {
      console.error("[SmartBite] Error fetching customer orders:", err);
      ordersListContainer.innerHTML = `
        <div class="orders-error-state">
          <i class="fa-solid fa-triangle-exclamation"></i>
          <p>Failed to retrieve orders: ${escapeHtml(err.message)}</p>
          <button class="btn btn-outline-dark btn-sm" id="retryFetchOrdersBtn" type="button">
            <i class="fa-solid fa-rotate-right"></i> Retry
          </button>
        </div>
      `;
      const retryBtn = document.getElementById("retryFetchOrdersBtn");
      if (retryBtn) retryBtn.addEventListener("click", loadCustomerOrders);
    } finally {
      if (refreshBtn) {
        refreshBtn.disabled = false;
        refreshBtn.innerHTML = `<i class="fa-solid fa-arrows-rotate"></i> Refresh`;
      }
    }
  }

  // Open Orders Modal and immediately load customer orders
  function openOrdersModal() {
    openModal(ordersModal);
    loadCustomerOrders();
  }

  // Navbar Modal Triggers
  if (loginModalBtn) {
    loginModalBtn.addEventListener("click", () => {
      openModal(loginModal);
      switchAuthTab("login");
    });
  }
  if (adminModalBtn) {
    adminModalBtn.addEventListener("click", () => {
      if (state.demoMode || (authState.isLoggedIn && authState.role === "ADMIN")) {
        openAdminModal();
      } else {
        openModal(loginModal);
        switchAuthTab("admin");
      }
    });
  }
  const mobileLoginBtn = document.getElementById("mobileLoginBtn");
  const mobileAdminBtn = document.getElementById("mobileAdminBtn");
  if (mobileLoginBtn) {
    mobileLoginBtn.addEventListener("click", () => {
      openModal(loginModal);
      switchAuthTab("login");
    });
  }
  if (mobileAdminBtn) {
    mobileAdminBtn.addEventListener("click", () => {
      if (state.demoMode || (authState.isLoggedIn && authState.role === "ADMIN")) {
        openAdminModal();
      } else {
        openModal(loginModal);
        switchAuthTab("admin");
      }
    });
  }
  if (navOrdersBtn) navOrdersBtn.addEventListener("click", openOrdersModal);
  if (heroTrackBtn) heroTrackBtn.addEventListener("click", openOrdersModal);

  // Footer Orders links
  document.querySelectorAll('.demo-trigger[data-target="orders"]').forEach(link => {
    link.addEventListener("click", openOrdersModal);
  });

  // Track in My Orders button from Success Modal
  const viewMyOrdersAfterSuccessBtn = document.getElementById("viewMyOrdersAfterSuccessBtn");
  if (viewMyOrdersAfterSuccessBtn) {
    viewMyOrdersAfterSuccessBtn.addEventListener("click", () => {
      closeModal(orderSuccessModal);
      openOrdersModal();
    });
  }

  // Orders Search & Refresh Event Listeners
  const searchOrdersBtn = document.getElementById("searchOrdersBtn");
  const refreshOrdersBtn = document.getElementById("refreshOrdersBtn");
  const ordersStudentIdInput = document.getElementById("ordersStudentIdInput");

  if (searchOrdersBtn) searchOrdersBtn.addEventListener("click", loadCustomerOrders);
  if (refreshOrdersBtn) refreshOrdersBtn.addEventListener("click", loadCustomerOrders);
  if (ordersStudentIdInput) {
    ordersStudentIdInput.addEventListener("keydown", (e) => {
      if (e.key === "Enter") {
        e.preventDefault();
        loadCustomerOrders();
      }
    });
  }

  /* ============================================================================
     9b. KITCHEN & ADMIN DASHBOARD MANAGEMENT
     ============================================================================ */
  const adminState = {
    orders: [],
    stats: null,
    statusFilter: "ALL",
    tableFilter: "ALL",
    stockSearch: "",
    activeTab: "orders",
    refreshTimer: null
  };

  // DOM Elements for Admin Modal
  const adminStatTotal = document.getElementById("adminStatTotal");
  const adminStatPending = document.getElementById("adminStatPending");
  const adminStatPreparing = document.getElementById("adminStatPreparing");
  const adminStatReady = document.getElementById("adminStatReady");
  const adminStatToday = document.getElementById("adminStatToday");
  const adminStatCompleted = document.getElementById("adminStatCompleted");
  const adminStatSales = document.getElementById("adminStatSales");
  const adminOrdersContainer = document.getElementById("adminOrdersContainer");
  const adminStockContainer = document.getElementById("adminStockContainer");
  const adminTabOrdersBtn = document.getElementById("adminTabOrdersBtn");
  const adminTabStockBtn = document.getElementById("adminTabStockBtn");
  const adminTabQrBtn = document.getElementById("adminTabQrBtn");
  const adminOrdersPane = document.getElementById("adminOrdersPane");
  const adminStockPane = document.getElementById("adminStockPane");
  const adminQrPane = document.getElementById("adminQrPane");
  const adminRefreshBtn = document.getElementById("adminRefreshBtn");
  const adminRefreshStockBtn = document.getElementById("adminRefreshStockBtn");
  const adminStockSearchInput = document.getElementById("adminStockSearchInput");
  const adminOrderFilterPills = document.getElementById("adminOrderFilterPills");
  const adminTableSelect = document.getElementById("adminTableSelect");
  const qrBaseUrlInput = document.getElementById("qrBaseUrlInput");
  const applyQrUrlBtn = document.getElementById("applyQrUrlBtn");
  const resetQrUrlBtn = document.getElementById("resetQrUrlBtn");
  const printAllTablesBtn = document.getElementById("printAllTablesBtn");

  // Seamless demo admin authentication helper
  async function ensureDemoAdminAuth() {
    if (authState.isLoggedIn && authState.role === "ADMIN" && authState.token) {
      return true;
    }

    try {
      console.log(`[SmartBite] Authenticating via demo admin endpoint: ${API_BASE_URL}/auth/demo/admin-login`);
      const response = await fetch(`${API_BASE_URL}/auth/demo/admin-login`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Accept": "application/json",
          "Content-Type": "application/json"
        }
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => null);
        throw new Error(errData?.message || `HTTP ${response.status} from demo login endpoint`);
      }

      const data = await response.json();
      if (!data || !data.token) {
        throw new Error("Demo admin response did not return a valid JWT token");
      }

      setAuthState(data.token, data);
      updateAuthUI();
      console.log(`[SmartBite] Authenticated as Demo Admin: ${data.name} (${data.role})`);
      return true;
    } catch (e) {
      console.error("[SmartBite] Demo admin authentication failed:", e);
      return false;
    }
  }

  async function openAdminModal() {
    if ((!authState.isLoggedIn || authState.role !== "ADMIN") && state.demoMode) {
      const authed = await ensureDemoAdminAuth();
      if (!authed) {
        showToast(
          "Demo Admin Login Failed",
          `Could not authenticate with ${API_BASE_URL}/auth/demo/admin-login. Check backend server.`,
          "fa-solid fa-triangle-exclamation"
        );
        return;
      }
    }

    if (!authState.isLoggedIn || authState.role !== "ADMIN") {
      showToast(
        "Admin Authentication Required",
        "Please sign in with authorized ROLE_ADMIN credentials to access Kitchen Dashboard.",
        "fa-solid fa-shield-halved"
      );
      openModal(loginModal);
      switchAuthTab("admin");
      return;
    }

    // Ensure any open login modal is closed
    if (loginModal) closeModal(loginModal);

    openModal(adminModal);
    loadAdminDashboard();
    renderAdminTableQRs();
    startAdminAutoRefresh();
  }

  function startAdminAutoRefresh() {
    stopAdminAutoRefresh();
    // Auto-refresh stats and orders every 3 seconds safely while modal is open
    adminState.refreshTimer = setInterval(() => {
      if (adminModal && adminModal.classList.contains("open")) {
        loadAdminDashboard(true);
      } else {
        stopAdminAutoRefresh();
      }
    }, 3000);
  }

  function stopAdminAutoRefresh() {
    if (adminState.refreshTimer) {
      clearInterval(adminState.refreshTimer);
      adminState.refreshTimer = null;
    }
  }

  async function loadAdminDashboard(silent = false) {
    if (!authState.isLoggedIn || authState.role !== "ADMIN") return;
    await Promise.all([
      fetchAdminStats(),
      fetchAdminOrders(silent)
    ]);
    renderAdminStock();
  }

  async function fetchAdminStats() {
    try {
      const response = await fetch(`${API_BASE_URL}/admin/stats`, {
        method: "GET",
        credentials: "include",
        headers: getAuthHeaders({ "Accept": "application/json" })
      });
      if (response.status === 401 || response.status === 403) {
        if (state.demoMode) {
          console.warn("[SmartBite Admin] Stats 401/403 in demo mode. Attempting token refresh...");
          const reauthed = await ensureDemoAdminAuth();
          if (reauthed) {
            return fetchAdminStats();
          }
          return;
        }
        showToast("Admin Session Expired", "Please sign in again as Admin.", "fa-solid fa-lock");
        closeModal(adminModal);
        openModal(loginModal);
        switchAuthTab("admin");
        return;
      }
      if (!response.ok) return;
      const data = await response.json();
      adminState.stats = data;

      if (adminStatTotal) adminStatTotal.textContent = data.totalOrders ?? 0;
      if (adminStatPending) adminStatPending.textContent = data.pendingOrders ?? 0;
      if (adminStatPreparing) adminStatPreparing.textContent = data.preparingOrders ?? 0;
      if (adminStatReady) adminStatReady.textContent = data.readyOrders ?? 0;
      if (adminStatToday) adminStatToday.textContent = data.todayOrders ?? 0;
      if (adminStatCompleted) adminStatCompleted.textContent = data.completedOrders ?? 0;
      if (adminStatSales) {
        const salesVal = (data.totalSales !== undefined && data.totalSales !== null) ? data.totalSales : (data.todayRevenue || 0);
        adminStatSales.textContent = `₹${salesVal.toFixed(2)}`;
      }
    } catch (err) {
      console.warn("[SmartBite Admin] Stats fetch failed:", err);
    }
  }

  async function fetchAdminOrders(silent = false) {
    if (!adminOrdersContainer) return;
    if (!silent && adminOrdersContainer.children.length === 0) {
      adminOrdersContainer.innerHTML = `
        <div class="orders-loading-state">
          <i class="fa-solid fa-spinner fa-spin"></i>
          <span>Loading Kitchen Orders Queue...</span>
        </div>
      `;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/admin/orders`, {
        method: "GET",
        credentials: "include",
        headers: getAuthHeaders({ "Accept": "application/json" })
      });

      if (response.status === 401 || response.status === 403) {
        if (state.demoMode) {
          console.warn("[SmartBite Admin] Orders 401/403 in demo mode. Attempting session refresh...");
          const reauthed = await ensureDemoAdminAuth();
          if (reauthed) {
            return fetchAdminOrders(silent);
          }
          showToast("Admin Authorization Error", "Could not verify admin permissions. Check backend server.", "fa-solid fa-triangle-exclamation");
          return;
        }
        showToast("Admin Authorization Required", "Please sign in as Admin to access kitchen orders.", "fa-solid fa-lock");
        closeModal(adminModal);
        openModal(loginModal);
        switchAuthTab("admin");
        return;
      }

      if (!response.ok) {
        throw new Error(`Server returned HTTP ${response.status}`);
      }

      const orders = await response.json();
      adminState.orders = Array.isArray(orders) ? orders : [];
      renderAdminOrders();
    } catch (err) {
      console.error("[SmartBite Admin] Orders fetch failed:", err);
      if (!silent) {
        adminOrdersContainer.innerHTML = `
          <div class="orders-error-state">
            <i class="fa-solid fa-triangle-exclamation"></i>
            <p>Failed to load kitchen queue: ${escapeHtml(err.message)}</p>
            <button class="btn btn-outline-dark btn-sm" id="retryAdminOrdersBtn" type="button">
              <i class="fa-solid fa-rotate-right"></i> Retry
            </button>
          </div>
        `;
        const retryBtn = document.getElementById("retryAdminOrdersBtn");
        if (retryBtn) retryBtn.addEventListener("click", () => fetchAdminOrders(false));
      }
    }
  }

  function renderAdminOrders() {
    if (!adminOrdersContainer) return;

    let filtered = [...adminState.orders];

    // Status filter
    if (adminState.statusFilter !== "ALL") {
      filtered = filtered.filter(o => o.status === adminState.statusFilter);
    }

    // Table filter
    if (adminState.tableFilter && adminState.tableFilter !== "ALL") {
      const tFilter = adminState.tableFilter.toUpperCase();
      filtered = filtered.filter(o => {
        const tId = o.tableId ? o.tableId.toUpperCase() : "";
        const cId = o.customerIdentifier ? o.customerIdentifier.toUpperCase() : "";
        return tId === tFilter || cId.includes(tFilter);
      });
    }

    if (filtered.length === 0) {
      adminOrdersContainer.innerHTML = `
        <div class="orders-empty-state">
          <i class="fa-solid fa-bell-concierge"></i>
          <h4>No Orders Found</h4>
          <p>No orders match the selected filters (${adminState.statusFilter} • ${adminState.tableFilter}).</p>
        </div>
      `;
      return;
    }

    adminOrdersContainer.innerHTML = filtered.map(order => {
      const itemsList = Array.isArray(order.items) ? order.items : [];
      const statusClass = `status-${(order.status || "pending").toLowerCase()}`;
      const formattedTime = order.createdAt 
        ? new Date(order.createdAt).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' })
        : "Just now";
      const isReady = (order.status === "READY");
      const readyCardClass = isReady ? "order-card-ready-glow" : "";
      const tableBadge = order.tableId 
        ? `<span class="admin-card-table-badge"><i class="fa-solid fa-chair"></i> Table ${escapeHtml(order.tableId)}</span>`
        : `<span class="badge" style="background:#E5E7EB; color:#4B5563; font-weight:700;"><i class="fa-solid fa-user"></i> Counter</span>`;

      return `
        <div class="kitchen-order-card ${readyCardClass}" data-order-id="${order.id}">
          <div class="kitchen-card-header">
            <div class="kitchen-token-wrap">
              <span class="kitchen-token-badge">${escapeHtml(order.pickupToken)}</span>
              ${tableBadge}
              <span class="kitchen-order-id">#Order ${order.id}</span>
              <span class="status-pill ${statusClass}">${escapeHtml(order.status)}</span>
            </div>
            <div class="kitchen-meta-info">
              <span><i class="fa-solid fa-id-card"></i> ${escapeHtml(order.customerIdentifier || "Student")}</span>
              <span><i class="fa-solid fa-clock"></i> ${formattedTime}</span>
            </div>
          </div>

          <div class="kitchen-items-box">
            ${itemsList.map(item => `
              <div class="kitchen-item-row">
                <span><span class="item-qty">${item.quantity}x</span> ${escapeHtml(item.itemName)}</span>
                <span>₹${item.lineTotal.toFixed(2)}</span>
              </div>
            `).join("")}
          </div>

          <div class="kitchen-card-footer">
            <div class="kitchen-order-total">Total: ₹${order.grandTotal.toFixed(2)}</div>
            <div class="kitchen-actions">
              ${renderOrderActionButtons(order)}
            </div>
          </div>
        </div>
      `;
    }).join("");

    // Attach click listeners to all status transition buttons
    adminOrdersContainer.querySelectorAll("[data-action-status]").forEach(btn => {
      btn.addEventListener("click", () => {
        const orderId = btn.getAttribute("data-order-id");
        const token = btn.getAttribute("data-token");
        const nextStatus = btn.getAttribute("data-action-status");
        advanceOrderStatusAction(orderId, token, nextStatus);
      });
    });
  }

  function renderOrderActionButtons(order) {
    const id = order.id;
    const token = escapeHtml(order.pickupToken);

    switch (order.status) {
      case "PENDING":
        return `
          <button type="button" class="btn-kitchen-action btn-kitchen-prepare" data-order-id="${id}" data-token="${token}" data-action-status="PREPARING">
            <i class="fa-solid fa-fire-burner"></i> Start Preparing
          </button>
          <button type="button" class="btn-kitchen-action btn-kitchen-cancel" data-order-id="${id}" data-token="${token}" data-action-status="CANCELLED">
            <i class="fa-solid fa-ban"></i> Cancel
          </button>
        `;
      case "PREPARING":
        return `
          <button type="button" class="btn-kitchen-action btn-kitchen-ready" data-order-id="${id}" data-token="${token}" data-action-status="READY">
            <i class="fa-solid fa-bell"></i> Ready for Pickup 🔔
          </button>
          <button type="button" class="btn-kitchen-action btn-kitchen-cancel" data-order-id="${id}" data-token="${token}" data-action-status="CANCELLED">
            <i class="fa-solid fa-ban"></i> Cancel
          </button>
        `;
      case "READY":
        return `
          <button type="button" class="btn-kitchen-action btn-kitchen-complete" data-order-id="${id}" data-token="${token}" data-action-status="COMPLETED" style="background:#10B981;">
            <i class="fa-solid fa-circle-check"></i> Mark Completed ✔
          </button>
        `;
      case "COMPLETED":
        return `<span class="badge text-success" style="font-size:0.8rem; font-weight:700;"><i class="fa-solid fa-check-double"></i> Order Fulfilled</span>`;
      case "CANCELLED":
        return `<span class="badge text-danger" style="font-size:0.8rem; font-weight:700;"><i class="fa-solid fa-xmark"></i> Order Cancelled</span>`;
      default:
        return "";
    }
  }

  async function advanceOrderStatusAction(orderId, token, nextStatus) {
    if (!state.demoMode) {
      const confirmation = window.confirm(
        `Confirm status update:\n\nAdvance Order #${orderId} (Pickup Token: ${token}) to status "${nextStatus}"?`
      );
      if (!confirmation) return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/admin/orders/${orderId}/status`, {
        method: "PATCH",
        credentials: "include",
        headers: getAuthHeaders({
          "Content-Type": "application/json",
          "Accept": "application/json"
        }),
        body: JSON.stringify({ status: nextStatus })
      });

      if (response.status === 401 || response.status === 403) {
        showToast("Admin Authorization Required", "You must have ROLE_ADMIN privileges to modify order statuses.", "fa-solid fa-lock");
        closeModal(adminModal);
        openModal(loginModal);
        switchAuthTab("admin");
        return;
      }

      if (!response.ok) {
        const errJson = await response.json().catch(() => null);
        throw new Error(errJson?.message || `Server returned HTTP ${response.status}`);
      }

      showToast(
        "Order Status Updated",
        `Order #${orderId} (${token}) marked as ${nextStatus}.`,
        "fa-solid fa-circle-check"
      );

      // Refresh stats & queue immediately
      await Promise.all([
        fetchAdminStats(),
        fetchAdminOrders(true)
      ]);

      // If customer orders modal is open or student has active orders, update it too
      if (ordersModal && ordersModal.classList.contains("open")) {
        loadCustomerOrders();
      }
    } catch (err) {
      console.error("[SmartBite Admin] Status update failed:", err);
      showToast(
        "Status Update Failed",
        err.message || "Failed to update order status.",
        "fa-solid fa-circle-exclamation"
      );
    }
  }

  // Admin Tab Switcher Handler
  function switchAdminTab(tabKey) {
    adminState.activeTab = tabKey;
    [adminTabOrdersBtn, adminTabStockBtn, adminTabQrBtn].forEach(btn => {
      if (btn) btn.classList.toggle("active", btn.getAttribute("data-tab") === tabKey);
    });

    if (adminOrdersPane) adminOrdersPane.style.display = (tabKey === "orders" ? "block" : "none");
    if (adminStockPane) adminStockPane.style.display = (tabKey === "stock" ? "block" : "none");
    if (adminQrPane) adminQrPane.style.display = (tabKey === "qr" ? "block" : "none");

    if (tabKey === "orders") {
      fetchAdminOrders();
      fetchAdminStats();
    } else if (tabKey === "stock") {
      renderAdminStock();
    } else if (tabKey === "qr") {
      renderAdminTableQRs();
    }
  }

  if (adminTabOrdersBtn) adminTabOrdersBtn.addEventListener("click", () => switchAdminTab("orders"));
  if (adminTabStockBtn) adminTabStockBtn.addEventListener("click", () => switchAdminTab("stock"));
  if (adminTabQrBtn) adminTabQrBtn.addEventListener("click", () => switchAdminTab("qr"));

  if (adminTableSelect) {
    adminTableSelect.addEventListener("change", (e) => {
      adminState.tableFilter = e.target.value;
      renderAdminOrders();
    });
  }

  // Table QR Code Generation & Printable Management
  function renderAdminTableQRs() {
    const container = document.getElementById("adminQrCardsContainer");
    if (!container) return;

    const baseInput = document.getElementById("qrBaseUrlInput");
    if (baseInput && !baseInput.value) {
      baseInput.value = state.qrBaseUrl;
    }

    const tables = state.configuredTables || ["T01", "T02", "T03", "T04", "T05", "T06", "T07", "T08", "T09", "T10"];
    container.innerHTML = "";

    tables.forEach(tableId => {
      const card = document.createElement("div");
      card.className = "table-qr-card";
      card.setAttribute("data-table", tableId);

      const targetUrl = `${state.qrBaseUrl.replace(/\/+$/, '')}/?table=${encodeURIComponent(tableId)}`;

      card.innerHTML = `
        <div class="table-qr-card-header">
          <span class="qr-card-brand"><i class="fa-solid fa-burger"></i> SmartBite Canteen</span>
          <h3 class="qr-card-title">Table ${escapeHtml(tableId)}</h3>
        </div>
        <div class="qr-canvas-holder" id="qrCanvas_${tableId}"></div>
        <div class="qr-card-url-hint">${escapeHtml(targetUrl)}</div>
        <div class="qr-card-actions">
          <button type="button" class="btn btn-primary btn-sm download-qr-btn" data-table="${tableId}">
            <i class="fa-solid fa-download"></i> Save PNG
          </button>
          <button type="button" class="btn btn-cream btn-sm print-card-btn" data-table="${tableId}">
            <i class="fa-solid fa-print"></i> Print
          </button>
          <a href="?table=${tableId}" target="_blank" class="btn btn-outline-dark btn-sm" title="Test open customer menu for this table">
            <i class="fa-solid fa-up-right-from-square"></i> Open
          </a>
        </div>
      `;

      container.appendChild(card);

      const holder = card.querySelector(`#qrCanvas_${tableId}`);
      if (holder && window.QRCode) {
        new window.QRCode(holder, {
          text: targetUrl,
          width: 160,
          height: 160
        });
      }
    });

    // Attach download and print listeners
    container.querySelectorAll(".download-qr-btn").forEach(btn => {
      btn.addEventListener("click", () => {
        const tId = btn.getAttribute("data-table");
        const canvas = container.querySelector(`#qrCanvas_${tId} canvas`);
        if (canvas) {
          const a = document.createElement("a");
          a.download = `SmartBite_Table_${tId}_QR.png`;
          a.href = canvas.toDataURL("image/png");
          a.click();
        }
      });
    });

    container.querySelectorAll(".print-card-btn").forEach(btn => {
      btn.addEventListener("click", () => {
        const tId = btn.getAttribute("data-table");
        printSingleTableQrCard(tId);
      });
    });
  }

  function printSingleTableQrCard(tableId) {
    const printContainer = document.getElementById("printSheetContainer");
    if (!printContainer) return;
    const targetUrl = `${state.qrBaseUrl.replace(/\/+$/, '')}/?table=${encodeURIComponent(tableId)}`;

    printContainer.innerHTML = `
      <div class="printable-qr-card">
        <p style="font-weight:800; text-transform:uppercase; color:#EA580C; letter-spacing:0.1em; margin:0;">SmartBite Campus Canteen</p>
        <h2 style="font-size:2.2rem; font-weight:800; margin:0.4rem 0;">TABLE ${escapeHtml(tableId)}</h2>
        <p style="margin:0.2rem 0 0.8rem; color:#4B5563;">Scan QR with phone camera to order fresh food from your seat</p>
        <div id="printSingleCanvasHolder" style="margin: 0.5rem auto;"></div>
        <p style="font-family:monospace; font-size:0.8rem; color:#6B7280; margin-top:0.6rem;">${escapeHtml(targetUrl)}</p>
      </div>
    `;

    const holder = document.getElementById("printSingleCanvasHolder");
    if (holder && window.QRCode) {
      new window.QRCode(holder, { text: targetUrl, width: 220, height: 220 });
    }

    setTimeout(() => window.print(), 200);
  }

  function printAllTableCards() {
    const printContainer = document.getElementById("printSheetContainer");
    if (!printContainer) return;
    const tables = state.configuredTables || ["T01", "T02", "T03", "T04", "T05", "T06", "T07", "T08", "T09", "T10"];

    printContainer.innerHTML = "";
    tables.forEach(tableId => {
      const targetUrl = `${state.qrBaseUrl.replace(/\/+$/, '')}/?table=${encodeURIComponent(tableId)}`;
      const card = document.createElement("div");
      card.className = "printable-qr-card";
      card.innerHTML = `
        <p style="font-weight:800; text-transform:uppercase; color:#EA580C; letter-spacing:0.1em; margin:0;">SmartBite Campus Canteen</p>
        <h2 style="font-size:2rem; font-weight:800; margin:0.3rem 0;">TABLE ${escapeHtml(tableId)}</h2>
        <p style="margin:0.2rem 0 0.6rem; color:#4B5563; font-size:0.85rem;">Scan QR with phone camera • Skip the queue</p>
        <div id="printAllHolder_${tableId}" style="margin: 0.4rem auto;"></div>
        <p style="font-family:monospace; font-size:0.75rem; color:#6B7280; margin-top:0.4rem;">${escapeHtml(targetUrl)}</p>
      `;
      printContainer.appendChild(card);

      const holder = card.querySelector(`#printAllHolder_${tableId}`);
      if (holder && window.QRCode) {
        new window.QRCode(holder, { text: targetUrl, width: 170, height: 170 });
      }
    });

    setTimeout(() => window.print(), 250);
  }

  if (applyQrUrlBtn) {
    applyQrUrlBtn.addEventListener("click", () => {
      if (qrBaseUrlInput && qrBaseUrlInput.value.trim()) {
        state.qrBaseUrl = qrBaseUrlInput.value.trim();
        localStorage.setItem("smartbite_qr_base_url", state.qrBaseUrl);
        renderAdminTableQRs();
        showToast("QR Codes Updated", `Destination URL set to: ${state.qrBaseUrl}`, "fa-solid fa-circle-check");
      }
    });
  }

  if (resetQrUrlBtn) {
    resetQrUrlBtn.addEventListener("click", () => {
      state.qrBaseUrl = window.location.origin + window.location.pathname;
      localStorage.removeItem("smartbite_qr_base_url");
      if (qrBaseUrlInput) qrBaseUrlInput.value = state.qrBaseUrl;
      renderAdminTableQRs();
      showToast("URL Reset", "Restored default current browser location.", "fa-solid fa-arrows-rotate");
    });
  }

  if (printAllTablesBtn) {
    printAllTablesBtn.addEventListener("click", printAllTableCards);
  }

  function renderAdminStock() {
    if (!adminStockContainer) return;

    const sourceItems = (state.menuItems && state.menuItems.length > 0) ? state.menuItems : SAMPLE_MENU_ITEMS;
    let items = [...sourceItems];

    if (adminState.stockSearch.trim() !== "") {
      const q = adminState.stockSearch.toLowerCase().trim();
      items = items.filter(i =>
        i.name.toLowerCase().includes(q) ||
        (i.categoryLabel && i.categoryLabel.toLowerCase().includes(q))
      );
    }

    if (items.length === 0) {
      adminStockContainer.innerHTML = `
        <div class="orders-empty-state">
          <i class="fa-solid fa-magnifying-glass"></i>
          <h4>No matching dishes found</h4>
          <p>Try searching with another food keyword or click "+ Add Item" above.</p>
        </div>
      `;
      return;
    }

    adminStockContainer.innerHTML = items.map(item => {
      const isOut = Boolean(item.availability && item.availability.toLowerCase().includes("out of stock"));
      return `
        <div class="stock-item-row" data-stock-id="${item.id}">
          <div class="stock-item-info">
            <img 
              src="${item.image || item.imageUrl || FALLBACK_IMAGE_DATA}" 
              alt="${escapeHtml(item.name)}" 
              class="stock-item-img" 
              onerror="this.onerror=null; this.src='${FALLBACK_IMAGE_DATA}';"
            />
            <div class="stock-item-text">
              <div class="stock-item-name">${escapeHtml(item.name)} <span style="font-size:0.72rem; color:var(--color-brown-400); font-weight:400;">(${item.id})</span></div>
              <div class="stock-item-meta">
                <span>${item.categoryLabel || item.category}</span>
                <span>•</span>
                <span>${item.isVeg ? '<span style="color:#16A34A; font-weight:600;">Veg</span>' : '<span style="color:#DC2626; font-weight:600;">Non-Veg</span>'}</span>
                <span>•</span>
                <span style="font-weight:700;">₹${Number(item.price).toFixed(2)}</span>
              </div>
            </div>
          </div>

          <div class="stock-toggle-wrap">
            <button 
              type="button" 
              class="stock-toggle-btn ${isOut ? 'is-out-of-stock' : 'is-available'}" 
              data-item-id="${item.id}"
              data-item-name="${escapeHtml(item.name)}"
              data-current-avail="${isOut ? 'Out of Stock' : 'Available'}"
              title="Click to toggle availability"
            >
              <i class="fa-solid ${isOut ? 'fa-ban' : 'fa-circle-check'}"></i>
              <span>${isOut ? 'Out of Stock' : 'Available'}</span>
            </button>
            <button 
              type="button" 
              class="btn-item-action btn-item-edit" 
              data-edit-id="${item.id}"
              title="Edit dish details & price"
            >
              <i class="fa-solid fa-pen-to-square"></i> Edit
            </button>
            <button 
              type="button" 
              class="btn-item-action btn-item-delete" 
              data-delete-id="${item.id}"
              data-delete-name="${escapeHtml(item.name)}"
              title="Delete dish from menu"
            >
              <i class="fa-solid fa-trash-can"></i> Delete
            </button>
          </div>
        </div>
      `;
    }).join("");

    // Attach listeners to stock toggle buttons
    adminStockContainer.querySelectorAll("[data-item-id]").forEach(btn => {
      btn.addEventListener("click", () => {
        const id = btn.getAttribute("data-item-id");
        const name = btn.getAttribute("data-item-name");
        const currentAvail = btn.getAttribute("data-current-avail");
        toggleMenuItemStockAction(id, name, currentAvail);
      });
    });

    // Attach listeners to edit buttons
    adminStockContainer.querySelectorAll(".btn-item-edit").forEach(btn => {
      btn.addEventListener("click", () => {
        const id = btn.getAttribute("data-edit-id");
        openEditMenuItemModal(id);
      });
    });

    // Attach listeners to delete buttons
    adminStockContainer.querySelectorAll(".btn-item-delete").forEach(btn => {
      btn.addEventListener("click", () => {
        const id = btn.getAttribute("data-delete-id");
        const name = btn.getAttribute("data-delete-name");
        deleteMenuItemAction(id, name);
      });
    });
  }

  async function toggleMenuItemStockAction(itemId, itemName, currentAvailability) {
    const isCurrentlyOut = currentAvailability.toLowerCase().includes("out of stock");
    const nextAvailability = isCurrentlyOut ? "Available" : "Out of Stock";

    const confirmAction = window.confirm(
      `Confirm availability change:\n\nMark "${itemName}" as "${nextAvailability}"?`
    );
    if (!confirmAction) return;

    try {
      const response = await fetch(`${API_BASE_URL}/admin/menu/${itemId}/availability`, {
        method: "PATCH",
        credentials: "include",
        headers: getAuthHeaders({
          "Content-Type": "application/json",
          "Accept": "application/json"
        }),
        body: JSON.stringify({ availability: nextAvailability })
      });

      if (response.status === 401 || response.status === 403) {
        showToast("Admin Authorization Required", "You must have ROLE_ADMIN privileges to update menu availability.", "fa-solid fa-lock");
        closeModal(adminModal);
        openModal(loginModal);
        switchAuthTab("admin");
        return;
      }

      if (!response.ok) {
        const errJson = await response.json().catch(() => null);
        throw new Error(errJson?.message || `Server returned HTTP ${response.status}`);
      }

      const updated = await response.json();

      // Update in local state.menuItems
      if (state.menuItems) {
        const idx = state.menuItems.findIndex(i => i.id === itemId);
        if (idx !== -1) {
          state.menuItems[idx].availability = updated.availability || nextAvailability;
        }
      }

      showToast(
        "Menu Availability Updated",
        `"${itemName}" is now marked ${updated.availability || nextAvailability}.`,
        nextAvailability === "Available" ? "fa-solid fa-circle-check" : "fa-solid fa-ban"
      );

      // Re-render both admin stock view and public customer food grid immediately!
      renderAdminStock();
      filterAndRenderMenu();
    } catch (err) {
      console.error("[SmartBite Admin] Menu availability update failed:", err);
      showToast(
        "Availability Update Failed",
        err.message || "Failed to update item availability.",
        "fa-solid fa-triangle-exclamation"
      );
    }
  }

  // Menu Item Modal Elements & Handlers
  const menuItemModal = document.getElementById("menuItemModal");
  const menuItemForm = document.getElementById("menuItemForm");
  const adminAddNewItemBtn = document.getElementById("adminAddNewItemBtn");
  const menuItemModalTitle = document.getElementById("menuItemModalTitle");
  const menuItemModalSubtitle = document.getElementById("menuItemModalSubtitle");
  const menuItemAlertBox = document.getElementById("menuItemAlertBox");
  const saveMenuItemSubmitBtn = document.getElementById("saveMenuItemSubmitBtn");

  function openCreateMenuItemModal() {
    if (!menuItemForm) return;
    menuItemForm.reset();
    const editModeInput = document.getElementById("menuItemEditMode");
    const itemIdInput = document.getElementById("menuItemId");
    if (editModeInput) editModeInput.value = "create";
    if (itemIdInput) itemIdInput.value = "";
    const availInput = document.getElementById("menuItemAvailability");
    if (availInput) availInput.value = "Available";
    const ratingInput = document.getElementById("menuItemRating");
    if (ratingInput) ratingInput.value = "4.5";
    if (menuItemModalTitle) menuItemModalTitle.textContent = "Add Menu Item";
    if (menuItemModalSubtitle) menuItemModalSubtitle.textContent = "Create a new dish or beverage in canteen catalog";
    clearMenuItemAlert();
    openModal(menuItemModal);
  }

  function openEditMenuItemModal(itemId) {
    const item = (state.menuItems || []).find(i => i.id === itemId);
    if (!item || !menuItemForm) return;

    const editModeInput = document.getElementById("menuItemEditMode");
    const itemIdInput = document.getElementById("menuItemId");
    if (editModeInput) editModeInput.value = "edit";
    if (itemIdInput) itemIdInput.value = item.id;

    const nameInput = document.getElementById("menuItemName");
    const catInput = document.getElementById("menuItemCategory");
    const priceInput = document.getElementById("menuItemPrice");
    const vegInput = document.getElementById("menuItemIsVeg");
    const availInput = document.getElementById("menuItemAvailability");
    const prepInput = document.getElementById("menuItemPrepTime");
    const ratingInput = document.getElementById("menuItemRating");
    const imgInput = document.getElementById("menuItemImageUrl");
    const descInput = document.getElementById("menuItemDescription");

    if (nameInput) nameInput.value = item.name || "";
    if (catInput) catInput.value = item.category || "snacks";
    if (priceInput) priceInput.value = item.price || "";
    if (vegInput) vegInput.value = item.isVeg ? "true" : "false";
    if (availInput) availInput.value = item.availability || "Available";
    if (prepInput) prepInput.value = item.prepTime || "";
    if (ratingInput) ratingInput.value = item.rating || "4.5";
    if (imgInput) imgInput.value = item.imageUrl || item.image || "";
    if (descInput) descInput.value = item.description || "";

    if (menuItemModalTitle) menuItemModalTitle.textContent = `Edit: ${item.name}`;
    if (menuItemModalSubtitle) menuItemModalSubtitle.textContent = `Update item details & price for ID ${item.id}`;
    clearMenuItemAlert();
    openModal(menuItemModal);
  }

  function showMenuItemAlert(message, type = "error") {
    if (!menuItemAlertBox) return;
    menuItemAlertBox.className = `auth-alert auth-alert-${type}`;
    menuItemAlertBox.innerHTML = `
      <i class="${type === 'success' ? 'fa-solid fa-circle-check' : 'fa-solid fa-circle-exclamation'}"></i>
      <span>${escapeHtml(message)}</span>
    `;
    menuItemAlertBox.classList.remove("hidden");
  }

  function clearMenuItemAlert() {
    if (!menuItemAlertBox) return;
    menuItemAlertBox.innerHTML = "";
    menuItemAlertBox.className = "auth-alert hidden";
  }

  async function deleteMenuItemAction(itemId, itemName) {
    const confirmation = window.confirm(
      `Confirm Deletion:\n\nAre you sure you want to permanently delete "${itemName}" (ID: ${itemId}) from the canteen menu?`
    );
    if (!confirmation) return;

    try {
      const response = await fetch(`${API_BASE_URL}/admin/menu/${itemId}`, {
        method: "DELETE",
        credentials: "include",
        headers: getAuthHeaders({ "Accept": "application/json" })
      });

      if (response.status === 401 || response.status === 403) {
        showToast("Admin Authorization Required", "You must have ROLE_ADMIN privileges to delete menu items.", "fa-solid fa-lock");
        closeModal(adminModal);
        openModal(loginModal);
        switchAuthTab("admin");
        return;
      }

      if (!response.ok) {
        const errJson = await response.json().catch(() => null);
        throw new Error(errJson?.message || `Server returned HTTP ${response.status}`);
      }

      // Remove from state.menuItems
      if (state.menuItems) {
        state.menuItems = state.menuItems.filter(i => i.id !== itemId);
      }

      showToast("Menu Item Deleted", `"${itemName}" was removed from the canteen catalog.`, "fa-solid fa-trash-can");
      renderAdminStock();
      filterAndRenderMenu();
    } catch (err) {
      console.error("[SmartBite Admin] Item delete failed:", err);
      showToast("Delete Failed", err.message || "Failed to delete item.", "fa-solid fa-triangle-exclamation");
    }
  }

  if (adminAddNewItemBtn) {
    adminAddNewItemBtn.addEventListener("click", openCreateMenuItemModal);
  }

  if (menuItemForm) {
    menuItemForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      clearMenuItemAlert();

      const modeInput = document.getElementById("menuItemEditMode");
      const mode = modeInput ? modeInput.value : "create";
      const itemIdInput = document.getElementById("menuItemId");
      const itemId = itemIdInput ? itemIdInput.value : "";
      const name = document.getElementById("menuItemName")?.value.trim() || "";
      const category = document.getElementById("menuItemCategory")?.value || "snacks";
      const priceVal = parseFloat(document.getElementById("menuItemPrice")?.value);
      const isVeg = document.getElementById("menuItemIsVeg")?.value === "true";
      const availability = document.getElementById("menuItemAvailability")?.value || "Available";
      const prepTime = document.getElementById("menuItemPrepTime")?.value.trim() || "";
      const rating = parseFloat(document.getElementById("menuItemRating")?.value) || 4.5;
      const imageUrl = document.getElementById("menuItemImageUrl")?.value.trim() || "";
      const description = document.getElementById("menuItemDescription")?.value.trim() || "";

      if (!name) {
        showMenuItemAlert("Dish name is required.");
        return;
      }
      if (isNaN(priceVal) || priceVal < 0) {
        showMenuItemAlert("Please enter a valid price (greater than or equal to 0).");
        return;
      }

      const payload = {
        name,
        category,
        price: priceVal,
        isVeg,
        availability,
        prepTime: prepTime || "5-10 mins",
        rating,
        imageUrl,
        description
      };

      if (saveMenuItemSubmitBtn) {
        saveMenuItemSubmitBtn.disabled = true;
        saveMenuItemSubmitBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Saving...`;
      }

      try {
        const url = mode === "edit" ? `${API_BASE_URL}/admin/menu/${itemId}` : `${API_BASE_URL}/admin/menu`;
        const method = mode === "edit" ? "PUT" : "POST";

        const response = await fetch(url, {
          method,
          credentials: "include",
          headers: getAuthHeaders({
            "Content-Type": "application/json",
            "Accept": "application/json"
          }),
          body: JSON.stringify(payload)
        });

        if (response.status === 401 || response.status === 403) {
          showToast("Admin Authorization Required", "You must have ROLE_ADMIN privileges to modify menu items.", "fa-solid fa-lock");
          closeModal(menuItemModal);
          closeModal(adminModal);
          openModal(loginModal);
          switchAuthTab("admin");
          return;
        }

        if (!response.ok) {
          const errJson = await response.json().catch(() => null);
          throw new Error(errJson?.message || `Failed to save menu item (HTTP ${response.status})`);
        }

        const savedItem = await response.json();
        closeModal(menuItemModal);

        showToast(
          mode === "edit" ? "Menu Item Updated" : "Menu Item Added",
          `"${savedItem.name}" has been ${mode === "edit" ? "updated" : "added to the menu"}.`,
          "fa-solid fa-circle-check"
        );

        // Reload latest menu data from MySQL
        await loadMenuData();
        renderAdminStock();
      } catch (err) {
        console.error("[SmartBite Admin] Save menu item failed:", err);
        showMenuItemAlert(err.message || "Failed to save menu item.");
      } finally {
        if (saveMenuItemSubmitBtn) {
          saveMenuItemSubmitBtn.disabled = false;
          saveMenuItemSubmitBtn.innerHTML = `<i class="fa-solid fa-floppy-disk"></i> Save Menu Item`;
        }
      }
    });
  }

  // Admin Tab Switcher Events
  if (adminTabOrdersBtn && adminTabStockBtn) {
    adminTabOrdersBtn.addEventListener("click", () => {
      adminTabOrdersBtn.classList.add("active");
      adminTabStockBtn.classList.remove("active");
      if (adminOrdersPane) adminOrdersPane.style.display = "block";
      if (adminStockPane) adminStockPane.style.display = "none";
      adminState.activeTab = "orders";
    });

    adminTabStockBtn.addEventListener("click", () => {
      adminTabStockBtn.classList.add("active");
      adminTabOrdersBtn.classList.remove("active");
      if (adminStockPane) adminStockPane.style.display = "block";
      if (adminOrdersPane) adminOrdersPane.style.display = "none";
      adminState.activeTab = "stock";
      renderAdminStock();
    });
  }

  // Admin Order Filter Pills
  if (adminOrderFilterPills) {
    adminOrderFilterPills.querySelectorAll(".admin-pill").forEach(pill => {
      pill.addEventListener("click", () => {
        adminOrderFilterPills.querySelectorAll(".admin-pill").forEach(p => p.classList.remove("active"));
        pill.classList.add("active");
        adminState.statusFilter = pill.getAttribute("data-filter") || "ALL";
        renderAdminOrders();
      });
    });
  }

  // Stock Search Filter
  if (adminStockSearchInput) {
    adminStockSearchInput.addEventListener("input", (e) => {
      adminState.stockSearch = e.target.value;
      renderAdminStock();
    });
  }

  // Manual Refresh Buttons
  if (adminRefreshBtn) {
    adminRefreshBtn.addEventListener("click", async () => {
      adminRefreshBtn.disabled = true;
      adminRefreshBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Refreshing...`;
      await loadAdminDashboard(false);
      adminRefreshBtn.disabled = false;
      adminRefreshBtn.innerHTML = `<i class="fa-solid fa-arrows-rotate"></i> Refresh`;
      showToast("Kitchen Queue Refreshed", "Loaded latest orders and statistics from MySQL.", "fa-solid fa-arrows-rotate");
    });
  }

  if (adminRefreshStockBtn) {
    adminRefreshStockBtn.addEventListener("click", async () => {
      adminRefreshStockBtn.disabled = true;
      adminRefreshStockBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Reloading...`;
      await loadMenuData();
      renderAdminStock();
      adminRefreshStockBtn.disabled = false;
      adminRefreshStockBtn.innerHTML = `<i class="fa-solid fa-arrows-rotate"></i> Refresh Menu`;
      showToast("Menu Reloaded", "Refreshed item availability list from MySQL.", "fa-solid fa-boxes-stacked");
    });
  }

  // Mobile menu toggle
  if (mobileMenuToggle && navMenu) {
    mobileMenuToggle.addEventListener("click", () => {
      navMenu.classList.toggle("open");
      const isOpen = navMenu.classList.contains("open");
      mobileMenuToggle.innerHTML = isOpen 
        ? '<i class="fa-solid fa-xmark"></i>' 
        : '<i class="fa-solid fa-bars"></i>';
    });

    // Close mobile menu when clicking any nav link
    navMenu.querySelectorAll(".nav-link").forEach(link => {
      link.addEventListener("click", () => {
        navMenu.classList.remove("open");
        mobileMenuToggle.innerHTML = '<i class="fa-solid fa-bars"></i>';
      });
    });
  }

  /* ============================================================================
     9c. PHASE 3 AUTHENTICATION CONTROLLER (STUDENT & ADMIN)
     ============================================================================ */
  function clearAuthState() {
    authState.token = null;
    authState.user = null;
    authState.role = null;
    authState.isLoggedIn = false;
    sessionStorage.removeItem("smartbite_token");
    localStorage.removeItem("smartbite_token");
    localStorage.removeItem("smartbite_auth_user");
  }

  function setAuthState(token, user) {
    if (!token) return;
    authState.token = token;
    authState.user = user || null;
    authState.role = user?.role || "ADMIN";
    authState.isLoggedIn = true;
    sessionStorage.setItem("smartbite_token", token);
    localStorage.setItem("smartbite_token", token);
    if (user) {
      localStorage.setItem("smartbite_auth_user", JSON.stringify(user));
    }
  }

  function showAuthAlert(message, type = "error") {
    if (!authAlertBox) return;
    authAlertBox.className = `auth-alert auth-alert-${type}`;
    authAlertBox.innerHTML = `
      <i class="${type === 'success' ? 'fa-solid fa-circle-check' : 'fa-solid fa-circle-exclamation'}"></i>
      <span>${escapeHtml(message)}</span>
    `;
    authAlertBox.classList.remove("hidden");
  }

  function clearAuthAlert() {
    if (!authAlertBox) return;
    authAlertBox.innerHTML = "";
    authAlertBox.className = "auth-alert hidden";
  }

  function switchAuthTab(tab) {
    clearAuthAlert();
    const tabs = [
      { id: "login", btn: tabStudentLoginBtn, form: studentLoginForm },
      { id: "register", btn: tabStudentRegisterBtn, form: studentRegisterForm },
      { id: "admin", btn: tabAdminLoginBtn, form: adminLoginForm }
    ];

    tabs.forEach(t => {
      if (t.btn) t.btn.classList.toggle("active", t.id === tab);
      if (t.form) t.form.classList.toggle("hidden", t.id !== tab);
    });

    if (loginModalTitle && loginModalSubtitle && authModalIcon) {
      if (tab === "login") {
        loginModalTitle.textContent = "Student Sign In";
        loginModalSubtitle.textContent = "Access your campus canteen account & tokens";
        authModalIcon.innerHTML = '<i class="fa-solid fa-right-to-bracket"></i>';
        authModalIcon.style.backgroundColor = "";
        authModalIcon.style.color = "";
      } else if (tab === "register") {
        loginModalTitle.textContent = "Student Registration";
        loginModalSubtitle.textContent = "Join SmartBite with your college roll number";
        authModalIcon.innerHTML = '<i class="fa-solid fa-user-plus"></i>';
        authModalIcon.style.backgroundColor = "";
        authModalIcon.style.color = "";
      } else if (tab === "admin") {
        loginModalTitle.textContent = "Kitchen & Admin Portal";
        loginModalSubtitle.textContent = "Canteen managers & kitchen staff authentication";
        authModalIcon.innerHTML = '<i class="fa-solid fa-shield-halved"></i>';
        authModalIcon.style.backgroundColor = "#FEF3C7";
        authModalIcon.style.color = "#B45309";
      }
    }
  }

  function updateAuthUI() {
    if (authState.isLoggedIn && authState.user) {
      // Desktop Nav User Chip
      if (userNavChip) userNavChip.classList.remove("hidden");
      if (userNavName) userNavName.textContent = authState.user.name || authState.user.collegeRollNumber;
      if (userNavRole) {
        userNavRole.textContent = authState.user.role;
        userNavRole.classList.toggle("role-admin", authState.user.role === "ADMIN");
      }
      if (loginModalBtn) loginModalBtn.classList.add("hidden");

      // Mobile Nav
      if (mobileUserNavGroup) mobileUserNavGroup.classList.remove("hidden");
      if (mobileAuthNavGroup) mobileAuthNavGroup.classList.add("hidden");
      if (mobileUserName) mobileUserName.textContent = authState.user.name;
      if (mobileUserRole) mobileUserRole.textContent = authState.user.role;

      // Admin button styling
      if (adminModalBtn) {
        if (authState.user.role === "ADMIN") {
          adminModalBtn.classList.remove("btn-outline-brown");
          adminModalBtn.classList.add("btn-primary");
          adminModalBtn.title = "Open Kitchen Dashboard";
        } else {
          adminModalBtn.classList.add("btn-outline-brown");
          adminModalBtn.classList.remove("btn-primary");
        }
      }
    } else {
      // Unauthenticated state
      if (userNavChip) userNavChip.classList.add("hidden");
      if (loginModalBtn) loginModalBtn.classList.remove("hidden");

      if (mobileUserNavGroup) mobileUserNavGroup.classList.add("hidden");
      if (mobileAuthNavGroup) mobileAuthNavGroup.classList.remove("hidden");

      if (adminModalBtn) {
        adminModalBtn.classList.add("btn-outline-brown");
        adminModalBtn.classList.remove("btn-primary");
      }
    }

    // Update orders modal header if open
    const ordersAuthUserLabel = document.getElementById("ordersAuthUserLabel");
    const ordersAuthStatusTitle = document.getElementById("ordersAuthStatusTitle");
    if (authState.isLoggedIn && authState.user) {
      if (ordersAuthStatusTitle) ordersAuthStatusTitle.textContent = "Private Order History (Verified)";
      if (ordersAuthUserLabel) {
        ordersAuthUserLabel.innerHTML = `Signed in as <strong>${escapeHtml(authState.user.name)}</strong> (${escapeHtml(authState.user.collegeRollNumber)}) • Orders scoped to your account`;
      }
    } else {
      if (ordersAuthStatusTitle) ordersAuthStatusTitle.textContent = "Guest / Unauthenticated View";
      if (ordersAuthUserLabel) {
        ordersAuthUserLabel.innerHTML = `Please sign in to view your orders and live pickup tokens.`;
      }
    }
  }

  // Restore authenticated session from backend on page load
  async function checkAuthSession() {
    try {
      const response = await fetch(`${API_BASE_URL}/auth/me`, {
        method: "GET",
        credentials: "include",
        headers: getAuthHeaders({ "Accept": "application/json" })
      });

      if (response.ok) {
        const user = await response.json();
        authState.user = user;
        authState.role = user.role;
        authState.isLoggedIn = true;
        console.log(`[SmartBite] Session active for ${user.email} (${user.role})`);
      } else {
        clearAuthState();
      }
    } catch (e) {
      console.warn("[SmartBite] Session verification skipped (offline or unauthenticated):", e);
      clearAuthState();
    } finally {
      updateAuthUI();
    }
  }

  // Auth Tab Switch Click Listeners
  if (tabStudentLoginBtn) tabStudentLoginBtn.addEventListener("click", () => switchAuthTab("login"));
  if (tabStudentRegisterBtn) tabStudentRegisterBtn.addEventListener("click", () => switchAuthTab("register"));
  if (tabAdminLoginBtn) tabAdminLoginBtn.addEventListener("click", () => switchAuthTab("admin"));
  if (switchToRegisterLink) switchToRegisterLink.addEventListener("click", () => switchAuthTab("register"));
  if (switchToLoginLink) switchToLoginLink.addEventListener("click", () => switchAuthTab("login"));

  // Student Login Form Submit
  if (studentLoginForm) {
    studentLoginForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      clearAuthAlert();

      const identifierInput = document.getElementById("loginIdentifier");
      const passwordInput = document.getElementById("loginPassword");
      const submitBtn = document.getElementById("studentLoginSubmitBtn");

      const identifier = identifierInput ? identifierInput.value.trim() : "";
      const password = passwordInput ? passwordInput.value : "";

      if (!identifier || !password) {
        showAuthAlert("Please provide both email/roll number and password.", "error");
        return;
      }

      submitBtn.disabled = true;
      const originalHtml = submitBtn.innerHTML;
      submitBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Signing in...`;

      try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
          },
          body: JSON.stringify({ identifier, password })
        });

        const data = await response.json().catch(() => null);

        if (!response.ok) {
          throw new Error(data?.message || "Invalid credentials. Please verify your details.");
        }

        setAuthState(data.token, data);
        updateAuthUI();
        closeModal(loginModal);

        showToast(
          "Welcome Back!",
          `Signed in successfully as ${data.name} (${data.collegeRollNumber}).`,
          "fa-solid fa-circle-check"
        );

        // If orders modal is open, refresh it
        if (ordersModal && ordersModal.classList.contains("open")) {
          loadCustomerOrders();
        }
      } catch (err) {
        showAuthAlert(err.message || "Login failed. Please check credentials.", "error");
      } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalHtml;
      }
    });
  }

  // Student Registration Form Submit
  if (studentRegisterForm) {
    studentRegisterForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      clearAuthAlert();

      const nameInput = document.getElementById("regFullName");
      const rollInput = document.getElementById("regRollNumber");
      const emailInput = document.getElementById("regEmail");
      const passInput = document.getElementById("regPassword");
      const submitBtn = document.getElementById("studentRegisterSubmitBtn");

      const name = nameInput ? nameInput.value.trim() : "";
      const collegeRollNumber = rollInput ? rollInput.value.trim() : "";
      const email = emailInput ? emailInput.value.trim() : "";
      const password = passInput ? passInput.value : "";

      if (!name || !collegeRollNumber || !email || !password) {
        showAuthAlert("Please fill in all registration fields.", "error");
        return;
      }

      if (password.length < 6) {
        showAuthAlert("Password must be at least 6 characters long.", "error");
        return;
      }

      submitBtn.disabled = true;
      const originalHtml = submitBtn.innerHTML;
      submitBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Creating Account...`;

      try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
          },
          body: JSON.stringify({ name, collegeRollNumber, email, password })
        });

        const data = await response.json().catch(() => null);

        if (!response.ok) {
          throw new Error(data?.message || "Registration failed. Please check inputs.");
        }

        setAuthState(data.token, data);
        updateAuthUI();
        closeModal(loginModal);

        showToast(
          "Registration Complete!",
          `Welcome to SmartBite, ${data.name}! Your student account is active.`,
          "fa-solid fa-circle-check"
        );
      } catch (err) {
        showAuthAlert(err.message || "Registration failed.", "error");
      } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalHtml;
      }
    });
  }

  // Admin Login Form Submit
  if (adminLoginForm) {
    adminLoginForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      clearAuthAlert();

      const idInput = document.getElementById("adminIdentifier");
      const passInput = document.getElementById("adminPassword");
      const submitBtn = document.getElementById("adminLoginSubmitBtn");

      const identifier = idInput ? idInput.value.trim() : "";
      const password = passInput ? passInput.value : "";

      if (!identifier || !password) {
        showAuthAlert("Please provide admin identifier and password.", "error");
        return;
      }

      submitBtn.disabled = true;
      const originalHtml = submitBtn.innerHTML;
      submitBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Verifying Admin Access...`;

      try {
        const response = await fetch(`${API_BASE_URL}/auth/admin/login`, {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
          },
          body: JSON.stringify({ identifier, password })
        });

        const data = await response.json().catch(() => null);

        if (!response.ok) {
          throw new Error(data?.message || "Invalid admin credentials.");
        }

        if (data.role !== "ADMIN") {
          throw new Error("Access Denied: This account does not possess ROLE_ADMIN privileges.");
        }

        setAuthState(data.token, data);
        updateAuthUI();
        closeModal(loginModal);

        showToast(
          "Admin Verified",
          `Logged in as Kitchen Manager (${data.name}).`,
          "fa-solid fa-shield-halved"
        );

        // Open Kitchen Dashboard directly upon admin login!
        openAdminModal();
      } catch (err) {
        showAuthAlert(err.message || "Admin authentication failed.", "error");
      } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalHtml;
      }
    });
  }

  // Logout Handler
  async function performLogout() {
    try {
      await fetch(`${API_BASE_URL}/auth/logout`, {
        method: "POST",
        credentials: "include",
        headers: getAuthHeaders()
      }).catch(() => null);
    } catch (_) {}

    clearAuthState();
    updateAuthUI();

    if (adminModal && adminModal.classList.contains("open")) {
      closeModal(adminModal);
    }

    if (ordersModal && ordersModal.classList.contains("open")) {
      loadCustomerOrders();
    }

    showToast(
      "Signed Out",
      "You have been safely signed out of SmartBite.",
      "fa-solid fa-arrow-right-from-bracket"
    );
  }

  if (logoutBtn) logoutBtn.addEventListener("click", performLogout);
  if (mobileLogoutBtn) mobileLogoutBtn.addEventListener("click", performLogout);

  // Global ESC Key to close drawers and modals
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      closeCartDrawer();
      document.querySelectorAll(".modal-backdrop.open").forEach(modal => closeModal(modal));
    }
  });

  /**
   * Fetch live menu items from Spring Boot REST API: GET /api/menu
   * Falls back gracefully to SAMPLE_MENU_ITEMS if the backend is temporarily unavailable.
   */
  async function loadMenuData() {
    const menuBadge = document.getElementById("menuDataBadge");
    try {
      if (menuBadge) {
        menuBadge.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Loading Campus Menu...`;
      }

      const response = await fetch(`${API_BASE_URL}/menu`, {
        method: "GET",
        headers: {
          "Accept": "application/json"
        }
      });

      if (!response.ok) {
        throw new Error(`Server returned HTTP ${response.status}`);
      }

      const data = await response.json();
      if (Array.isArray(data) && data.length > 0) {
        state.menuItems = data;
        state.isLive = true;
        if (menuBadge) {
          menuBadge.innerHTML = `<i class="fa-solid fa-circle-check text-success"></i> Live Menu (MySQL Connected)`;
        }
        console.log(`[SmartBite] Successfully loaded ${data.length} live menu items from MySQL API: ${API_BASE_URL}/menu`);
      } else {
        throw new Error("No menu records returned from API");
      }
    } catch (error) {
      console.warn(`[SmartBite] Backend API unreachable at ${API_BASE_URL}/menu. Using fallback sample menu data.`, error);
      state.menuItems = [...SAMPLE_MENU_ITEMS];
      state.isLive = false;
      if (menuBadge) {
        menuBadge.innerHTML = `<i class="fa-solid fa-triangle-exclamation text-warning"></i> Sample Menu Data (Offline Preview)`;
      }
      showToast(
        "Using Local Menu Preview",
        "Live backend connection unavailable. Showing sample canteen menu.",
        "fa-solid fa-circle-info"
      );
    } finally {
      filterAndRenderMenu();
    }
  }

  /* ============================================================================
     9d. LIVE CUSTOMER ORDER STATUS TRACKING & NOTIFICATIONS
     ============================================================================ */
  function updateCustomerOrderStepperUI(order) {
    if (!order) return;

    const statusBadge = document.getElementById("successOrderStatus");
    const statusText = (order.status || "PENDING").toUpperCase();
    if (statusBadge) {
      statusBadge.textContent = statusText;
      statusBadge.className = `order-status-badge status-${statusText.toLowerCase()}`;
    }

    const assignedTable = order.tableId || state.activeTable;
    const tableTagEl = document.getElementById("successOrderTableTag");
    if (tableTagEl) {
      if (assignedTable) {
        tableTagEl.innerHTML = `<i class="fa-solid fa-chair"></i> Table ${escapeHtml(assignedTable)}`;
        tableTagEl.style.display = "inline-flex";
      } else {
        tableTagEl.style.display = "none";
      }
    }

    const nodePending = document.getElementById("stepNodePending");
    const line1 = document.getElementById("stepLine1");
    const nodePreparing = document.getElementById("stepNodePreparing");
    const line2 = document.getElementById("stepLine2");
    const nodeReady = document.getElementById("stepNodeReady");
    const line3 = document.getElementById("stepLine3");
    const nodeCompleted = document.getElementById("stepNodeCompleted");
    const readyAlert = document.getElementById("orderReadyAlert");
    const readyAlertToken = document.getElementById("readyAlertToken");

    // Reset all step states
    [nodePending, nodePreparing, nodeReady, nodeCompleted].forEach(n => {
      if (n) {
        n.classList.remove("active", "done");
      }
    });
    [line1, line2, line3].forEach(l => {
      if (l) l.classList.remove("active", "done");
    });

    if (statusText === "PENDING") {
      if (nodePending) nodePending.classList.add("active");
    } else if (statusText === "PREPARING") {
      if (nodePending) nodePending.classList.add("done");
      if (line1) line1.classList.add("active");
      if (nodePreparing) nodePreparing.classList.add("active");
    } else if (statusText === "READY") {
      if (nodePending) nodePending.classList.add("done");
      if (line1) line1.classList.add("done");
      if (nodePreparing) nodePreparing.classList.add("done");
      if (line2) line2.classList.add("active");
      if (nodeReady) nodeReady.classList.add("active");
    } else if (statusText === "COMPLETED") {
      if (nodePending) nodePending.classList.add("done");
      if (line1) line1.classList.add("done");
      if (nodePreparing) nodePreparing.classList.add("done");
      if (line2) line2.classList.add("done");
      if (nodeReady) nodeReady.classList.add("done");
      if (line3) line3.classList.add("done");
      if (nodeCompleted) nodeCompleted.classList.add("done");
    }

    // Prominent "Ready for Pickup" In-Page Alert Banner
    if (readyAlert) {
      if (statusText === "READY") {
        readyAlert.classList.remove("hidden");
        if (readyAlertToken) {
          readyAlertToken.textContent = `#${order.pickupToken || ""}`;
        }
      } else {
        readyAlert.classList.add("hidden");
      }
    }
  }

  function playReadyChime() {
    try {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (!AudioCtx) return;
      const ctx = new AudioCtx();
      if (ctx.state === "suspended") {
        ctx.resume();
      }
      const now = ctx.currentTime;
      // High chime 1: 880Hz (A5)
      const osc1 = ctx.createOscillator();
      const gain1 = ctx.createGain();
      osc1.type = "sine";
      osc1.frequency.setValueAtTime(880, now);
      gain1.gain.setValueAtTime(0.2, now);
      gain1.gain.exponentialRampToValueAtTime(0.001, now + 0.35);
      osc1.connect(gain1);
      gain1.connect(ctx.destination);
      osc1.start(now);
      osc1.stop(now + 0.35);

      // High chime 2: 1318.5Hz (E6)
      const osc2 = ctx.createOscillator();
      const gain2 = ctx.createGain();
      osc2.type = "sine";
      osc2.frequency.setValueAtTime(1318.5, now + 0.16);
      gain2.gain.setValueAtTime(0.25, now + 0.16);
      gain2.gain.exponentialRampToValueAtTime(0.001, now + 0.65);
      osc2.connect(gain2);
      gain2.connect(ctx.destination);
      osc2.start(now + 0.16);
      osc2.stop(now + 0.65);
    } catch (e) {
      console.warn("[SmartBite] Web Audio chime could not play:", e);
    }
  }

  function triggerReadyNotification(order) {
    playReadyChime();

    showToast(
      "Your Order is Ready! 🔔",
      `Token #${order.pickupToken} is ready for pickup! Please collect your order from the canteen counter.`,
      "fa-solid fa-bell"
    );

    // Browser desktop notification if permitted
    if ("Notification" in window) {
      if (Notification.permission === "granted") {
        try {
          new Notification("SmartBite: Order Ready for Pickup! 🔔", {
            body: `Token #${order.pickupToken}: Your food is freshly prepared and ready for collection!`,
            icon: "favicon.ico"
          });
        } catch (_) {}
      } else if (Notification.permission === "default") {
        Notification.requestPermission().catch(() => null);
      }
    }
  }

  async function pollCustomerOrderStatus(token) {
    if (!token) return;
    try {
      const response = await fetch(`${API_BASE_URL}/orders/token/${encodeURIComponent(token)}`, {
        method: "GET",
        headers: { "Accept": "application/json" }
      });
      if (!response.ok) return;

      const order = await response.json();
      updateCustomerOrderStepperUI(order);

      // Check for READY status transition
      if (order.status === "READY" && state.lastPolledStatus !== "READY") {
        triggerReadyNotification(order);
      }
      state.lastPolledStatus = order.status;

      // When completed or cancelled, slow down polling frequency
      if (order.status === "COMPLETED" || order.status === "CANCELLED") {
        if (state.customerPollingTimer) {
          clearInterval(state.customerPollingTimer);
          state.customerPollingTimer = setInterval(() => pollCustomerOrderStatus(token), 10000);
        }
      }
    } catch (err) {
      console.warn("[SmartBite] Order token polling check skipped:", err);
    }
  }

  function startCustomerOrderTracking(token) {
    if (!token) return;
    state.activeTrackingToken = token;
    state.lastPolledStatus = null;
    localStorage.setItem("smartbite_active_tracking_token", token);

    // Request desktop notification permission gracefully
    if ("Notification" in window && Notification.permission === "default") {
      Notification.requestPermission().catch(() => null);
    }

    if (state.customerPollingTimer) {
      clearInterval(state.customerPollingTimer);
    }
    pollCustomerOrderStatus(token);
    state.customerPollingTimer = setInterval(() => {
      pollCustomerOrderStatus(token);
    }, 2500);
  }

  /* ============================================================================
     9e. DEMO MODE & TABLE ROUTING
     ============================================================================ */
  async function loadSystemConfig() {
    try {
      const res = await fetch(`${API_BASE_URL}/config`, {
        method: "GET",
        headers: { "Accept": "application/json" }
      });
      if (res.ok) {
        const config = await res.json();
        if (config.demoMode !== undefined) state.demoMode = Boolean(config.demoMode);
        if (Array.isArray(config.tables) && config.tables.length > 0) {
          state.configuredTables = config.tables;
        }

        const adminTableSelect = document.getElementById("adminTableSelect");
        if (adminTableSelect) {
          const currentVal = adminTableSelect.value;
          adminTableSelect.innerHTML = `<option value="ALL">All Tables (T01 - T10)</option>` +
            state.configuredTables.map(t => `<option value="${t}">Table ${t}</option>`).join("") +
            `<option value="COUNTER">Counter / Takeaway</option>`;
          adminTableSelect.value = currentVal || "ALL";
        }
      }
    } catch (e) {
      console.warn("[SmartBite] Could not load system config from backend, using defaults:", e);
    }
  }

  function initTableModeAndNavigation() {
    const urlParams = new URLSearchParams(window.location.search);
    const tableParam = urlParams.get("table");
    const tableBanner = document.getElementById("tableWelcomeBanner");
    const bannerTableId = document.getElementById("bannerTableId");
    const bannerTableHeading = document.getElementById("bannerTableHeading");
    const trayTableBadge = document.getElementById("trayTableBadge");
    const trayTableLabel = document.getElementById("trayTableLabel");
    const trayCustomerInputWrap = document.getElementById("trayCustomerInputWrap");
    const trayCustomerNameInput = document.getElementById("trayCustomerNameInput");

    if (tableParam) {
      const normalized = tableParam.trim().toUpperCase();
      const isValid = (state.configuredTables || []).includes(normalized);

      if (isValid) {
        state.activeTable = normalized;
        if (bannerTableId) bannerTableId.textContent = `Table ${normalized}`;
        if (bannerTableHeading) bannerTableHeading.textContent = `Table ${normalized}`;
        if (tableBanner) tableBanner.classList.remove("hidden");

        if (trayTableBadge && trayTableLabel) {
          trayTableLabel.textContent = `Table ${normalized}`;
          trayTableBadge.classList.remove("hidden");
        }
        if (trayCustomerInputWrap) {
          trayCustomerInputWrap.classList.remove("hidden");
        }
        if (trayCustomerNameInput) {
          trayCustomerNameInput.placeholder = `Table ${normalized} Guest (or enter your name)`;
        }
        console.log(`[SmartBite] Customer menu active for Table ${normalized}`);
      } else {
        if (bannerTableHeading) {
          bannerTableHeading.innerHTML = `<span style="color:#DC2626;">Invalid Table "${escapeHtml(tableParam)}"</span>`;
        }
        if (tableBanner) tableBanner.classList.remove("hidden");
      }
    } else {
      // No table param in URL
      if (tableBanner) tableBanner.classList.add("hidden");
      if (trayTableBadge) trayTableBadge.classList.add("hidden");
      if (trayCustomerInputWrap) trayCustomerInputWrap.classList.remove("hidden");
    }
  }

  // Bind Table and Demo UI shortcut triggers
  const switchTableBtn = document.getElementById("switchTableBtn");
  if (switchTableBtn) {
    switchTableBtn.addEventListener("click", () => {
      const newTable = window.prompt(`Enter table number to switch to (${(state.configuredTables || []).join(", ")}):`, state.activeTable || "T01");
      if (newTable) {
        const trimmed = newTable.trim().toUpperCase();
        if ((state.configuredTables || []).includes(trimmed)) {
          window.location.search = `?table=${trimmed}`;
        } else {
          alert(`Invalid table "${newTable}". Allowed tables: ${(state.configuredTables || []).join(", ")}`);
        }
      }
    });
  }

  const topNavAdminShortcutBtn = document.getElementById("topNavAdminShortcutBtn");
  if (topNavAdminShortcutBtn) {
    topNavAdminShortcutBtn.addEventListener("click", () => {
      openAdminModal();
    });
  }

  const demoBannerAdminLink = document.getElementById("demoBannerAdminLink");
  if (demoBannerAdminLink) {
    demoBannerAdminLink.addEventListener("click", (e) => {
      e.preventDefault();
      openAdminModal();
    });
  }

  /* ============================================================================
     10. INITIALIZATION
     ============================================================================ */
  async function initializeApp() {
    // 1. Immediately setup table mode banner synchronously for zero-latency rendering
    initTableModeAndNavigation();

    // 2. Fetch backend configuration (demo mode flag, dynamic tables list)
    await loadSystemConfig();

    const urlParams = new URLSearchParams(window.location.search);
    const tableParam = urlParams.get("table");

    if (tableParam) {
      // Customer table page: http://localhost:3000/?table=T01
      initTableModeAndNavigation();
      renderCategoryPills();
      updateCartUI();
      await checkAuthSession();
      await loadMenuData();
    } else if (state.demoMode) {
      // Demo mode landing without table parameter: http://localhost:3000/
      console.log("[SmartBite Demo Mode] Root URL opened in demo mode. Auto-authenticating Demo Admin...");
      renderCategoryPills();
      await loadMenuData();
      updateCartUI();
      initTableModeAndNavigation();

      const authed = await ensureDemoAdminAuth();
      if (authed) {
        await openAdminModal();
      } else {
        console.error("[SmartBite Demo Mode] Demo Admin auto-login failed during startup.");
        showToast(
          "Demo Admin Login Failed",
          `Could not authenticate with ${API_BASE_URL}/auth/demo/admin-login. Check backend server.`,
          "fa-solid fa-triangle-exclamation"
        );
      }
    } else {
      // Non-demo mode root URL: http://localhost:3000/
      await checkAuthSession();
      renderCategoryPills();
      await loadMenuData();
      updateCartUI();
      initTableModeAndNavigation();
    }

    // Resume tracking if customer had an active order in this session
    if (state.activeTrackingToken) {
      startCustomerOrderTracking(state.activeTrackingToken);
    }
  }

  initializeApp();

  console.log(
    "%c SmartBite %c QR Smart Campus Canteen Active • Table QR Codes & Live Kitchen Board Ready ",
    "background: #EA580C; color: #FFF; padding: 4px 8px; border-radius: 4px 0 0 4px; font-weight: bold;",
    "background: #2E1B13; color: #FFEDD5; padding: 4px 8px; border-radius: 0 4px 4px 0;"
  );
});


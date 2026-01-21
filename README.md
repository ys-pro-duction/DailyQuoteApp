# QuoteVault 📚

A full-featured quote discovery and collection mobile application built with Kotlin and Jetpack Compose. QuoteVault allows users to browse, save, and share inspiring quotes with cloud synchronization across devices.

![Badge](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat&logo=kotlin)
![Badge](https://img.shields.io/badge/Compose-1.7.0-4285F4?style=flat&logo=android)
![Badge](https://img.shields.io/badge/Supabase-3.1.1-3ECF8E?style=flat&logo=supabase)
![Badge](https://img.shields.io/badge/Platform-Android-34A853?style=flat&logo=android)
![Badge](https://img.shields.io/badge/API-28%2B-FF6F00?style=flat)

## 🎨 UI Designs

Design mockups created using [Stitch - AI Design Tool](https://stitch.withgoogle.com)

[Potential Design Files Placeholder - Link to your Figma/Stitch designs]

## ✨ Features Implemented

### Authentication & User Accounts
- ✅ Sign up with email/password
- ✅ Login/logout functionality  
- ✅ Password reset flow with email verification
- ✅ User profile screen (name, email)
- ✅ Session persistence (stay logged in)
- **Tech Stack:** Supabase Auth

### Quote Browsing & Discovery
- ✅ Home feed displaying quotes
- ✅ Browse quotes by category (Motivation, Love, Success, Wisdom, Humor)
- ✅ Loading states and empty states handled gracefully
- **Data Source:** Supabase Database (seeded with 100+ quotes)
- **Tech Stack:** Supabase Postgrest, Jetpack Compose LazyColumn

### Favorites & Collections
- ✅ Save quotes to favorites (heart icon)
- ✅ View all favorited quotes in dedicated screen
- ✅ Create custom collections ("Morning Motivation", "Work Quotes", etc.)
- ✅ Add/remove quotes from collections
- ✅ Cloud sync across devices when logged in
- **Tech Stack:** Supabase Database (user_favorites, collections, collection_items tables)

### Daily Quote & Notifications
- ✅ "Quote of the Day" prominently displayed on home screen
- ✅ Quote of the day changes daily (client-side logic)
- ✅ Local push notification for daily quote
- ✅ User can set preferred notification time in settings
- **Tech Stack:** AlarmManager, BroadcastReceiver, Notification Manager

### Sharing & Export
- ✅ Share quote as text via system share sheet (WhatsApp, Instagram, Twitter, etc.)
- ✅ Generate shareable quote card (quote + author on styled background)
- ✅ Save quote card as image to device
- ✅ 3 different card styles/templates to choose from
- **Tech Stack:** Canvas-based image generation, Android ShareSheet

### Personalization & Settings
- ✅ Dark mode / Light mode toggle
- ✅ Font size adjustment for quotes
- ✅ Settings persist locally and sync to user profile
- **Tech Stack:** SharedPreferences, Material 3 Dynamic Color

### Widget
- ✅ Home screen widget displaying current quote of the day
- ✅ Widget updates daily with new quotes
- ✅ Tapping widget opens the app to that quote
- **Tech Stack:** Android AppWidget, RemoteViews

### Code Quality & Architecture
- ✅ Clean project structure with separation of concerns
- ✅ MVVM architecture with ViewModels and StateFlow
- ✅ Consistent naming conventions

## 🏗️ Architecture

QuoteVault follows the MVVM (Model-View-ViewModel) architecture pattern with clean separation of concerns:

```
QuoteBro/
├── data/
│   ├── model/               # Data models (Quote, User, Collection, etc.)
│   ├── remote/              # Supabase client and API layer
│   └── repository/          # Data repositories (AuthRepository, QuoteRepository)
├── ui/
│   ├── home/                # Home screen with quotes feed
│   ├── favorites/           # Saved quotes screen
│   ├── settings/            # Settings and personalization
│   ├── login/               # Authentication screens (Login, SignUp, Reset Password)
│   ├── share/               # Quote sharing and export
│   ├── theme/               # Material 3 theme and custom icons
│   └── navigation/          # Navigation graph and routes
├── util/                    # Utilities (Notifications, AlarmScheduler)
├── widget/                  # Home screen widget
└── receiver/                # Broadcast receivers for notifications
```

### Key Architectural Patterns:
- **MVVM**: ViewModels manage UI state and business logic
- **Repository Pattern**: Single source of truth for data operations
- **Jetpack Compose**: Modern declarative UI toolkit
- **StateFlow + Compose State**: Reactive state management
- **Single Activity Architecture**: Navigation handled through Jetpack Navigation

## 📋 Prerequisites

- **Kotlin**: 2.0.21
- **Android SDK**: API 28 (Android 9.0) minimum, API 36 (Android 15) target
- **Gradle**: 8.13.2
- **Supabase**: Free-tier account for backend services

## 🚀 Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/QuoteVault.git
cd QuoteVault
```

### 2. Set Up Supabase Backend

#### Create a Supabase Project
1. Go to [supabase.com](https://supabase.com) and sign up/login
2. Create a new project
3. Note your project URL and anon key from Settings > API

#### Create Database Tables

Run these SQL queries in the Supabase SQL Editor:

```sql
-- Create Quotes Table
create table quotes (
  id bigint generated by default as identity primary key,
  content text not null,
  author text not null,
  category text not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Create Profiles (Triggered on Auth Sign up)
create table profiles (
  id uuid references auth.users not null primary key,
  full_name text,
  avatar_url text,
  updated_at timestamp with time zone
);

-- Create Favorites
create table favorites (
  id bigint generated by default as identity primary key,
  user_id uuid references auth.users not null,
  quote_id bigint references quotes(id) not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null,
  unique(user_id, quote_id)
);

-- Create Collections
create table collections (
  id bigint generated by default as identity primary key,
  user_id uuid references auth.users not null,
  name text not null,
  description text,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Create Collection Items (junction table)
create table collection_items (
  id bigint generated by default as identity primary key,
  collection_id bigint references collections(id) on delete cascade not null,
  quote_id bigint references quotes(id) on delete cascade not null,
  added_at timestamp with time zone default timezone('utc'::text, now()) not null,
  unique(collection_id, quote_id)
);

-- Enable Row Level Security
alter table quotes enable row level security;
alter table profiles enable row level security;
alter table favorites enable row level security;
alter table collections enable row level security;
alter table collection_items enable row level security;

-- Policies for Quotes
create policy "Public quotes are viewable by everyone." 
  on quotes for select using (true);

create policy "Authenticated users can insert quotes." 
  on quotes for insert with check (auth.role() = 'authenticated');

-- Policies for Profiles
create policy "Public profiles are viewable by everyone." 
  on profiles for select using (true);

create policy "Users can update their own profile." 
  on profiles for update using (auth.uid() = id);

-- Policies for Favorites
create policy "Users can insert their own favorites." 
  on favorites for insert with check (auth.uid() = user_id);

create policy "Users can view their own favorites." 
  on favorites for select using (auth.uid() = user_id);

create policy "Users can delete their own favorites." 
  on favorites for delete using (auth.uid() = user_id);

-- Policies for Collections
create policy "Users can create their own collections." 
  on collections for insert with check (auth.uid() = user_id);

create policy "Users can view their own collections." 
  on collections for select using (auth.uid() = user_id);

create policy "Users can update their own collections." 
  on collections for update using (auth.uid() = user_id);

create policy "Users can delete their own collections." 
  on collections for delete using (auth.uid() = user_id);

--  Policies for Collection Items
create policy "Users can manage items in their collections." 
  on collection_items for all using (
    exists (
      select 1 from collections 
      where collections.id = collection_items.collection_id 
      and collections.user_id = auth.uid()
    )
  );
```

#### Seed Quotes Data

Run this script to add sample quotes:

```sql
-- Insert sample quotes
INSERT INTO quotes (content, author, category) VALUES
('The only way to do great work is to love what you do.', 'Steve Jobs', 'Motivation');
-- ... Add 100+ more quotes across all categories
```

### 3. Configure Supabase in App

Update the Supabase credentials in `local.properties`:

```javascript
SUPABASE_URL=https://example.supabase.co
SUPABASE_KEY=examplesb_publishable_n54Sa
```

### 4. Enable Email Authentication  

1. Go to Supabase Dashboard > Authentication > Providers
2. Enable Email provider
3. Configure email templates and SMTP settings (or use Supabase default)

### 5. Build and Run

```bash
# Clean and build the project
./gradlew clean build

# Install on connected device
./gradlew installDebug

# Or use Android Studio: Build > Rebuild Project, then Run
```

### 6. Configure Email Deep Linking (Optional, for password reset)

If you want password reset to work with your app:

1. In Supabase Dashboard > Authentication > URL Configuration
2. Set Site URL to: `com.btech_dev.quotebro:/`
3. Set Redirect URLs to: `com.btech_dev.quotebro://reset-callback.quotebro/*`

## 🛠️ Dependencies

Key dependencies managed through Gradle Version Catalog:

```toml
# UI
androidx.compose.bom = 2024.09.00
androidx.compose.material3
androidx.navigation.compose = 2.8.4

# Backend
supabase-bom = 3.1.1
ktor-client-android = 3.0.0
```

See `gradle/libs.versions.toml` for complete dependency list.

## 📱 Screenshots

### Home Screen
> [Screenshot placeholder - Home screen with Quote of the Day and feed]

### Categories
> [Screenshot placeholder - Category chips and filtered quotes]

### Favorites
> [Screenshot placeholder - Saved quotes list]

### Collections
> [Screenshot placeholder - Collections management]

### Quote Sharing
> [Screenshot placeholder - Share card styles and export options]

### Settings
> [Screenshot placeholder - Theme, font size, and notification settings]

### Widget
> [Screenshot placeholder - Home screen widget]

## 🤖 AI Coding Approach & Workflow

### Development Workflow

1. **Planning Phase**
   - Used AI to break down the assignment requirements into actionable tasks
   - Generated project structure and architecture recommendations
   - Created database schema with RLS policies

2. **Implementation Phase**
   - Define project structure with empty code files (for ai agent to understand project)
   - Generate compose ui with design image (one at a time)
   - Refine UI manually
   - Implement Supabse Auth and Database with Ai agent
   - Continue to generate logic and implement and refine manually


### AI Tools Used

- **Antigravity**: Primary AI coding assistant for code generation
- **Android Studio Built in Ai**: Auto complete single and multiple line of code
- **Firebender (ide extension)**: Agent use for small and complex code refactoring
- **Stitch**: Initial design mockups and flow visualization

### How AI Improved Development

- **Speed**: Completed project in few days
- **Code Quality**: Generated clean, idiomatic Kotlin and Compose code
- **Architecture**: AI suggested MVVM pattern which provided excellent separation of concerns
- **Error Handling**: AI-generated comprehensive error handling and edge case management
- **Best Practices**: Followed Android development best practices without manual research

## 🎯 Known Limitations

### Minor Items
- Offline mode is limited - some features require network connectivity
- Widget refresh interval depends on system update constraints (updates every few hours)
- Quote images are gradient-based backgrounds (no actual quote images)
- Limited to 1000 quotes in local cache

### Potential Improvements
- Add full offline support with local database (Room)
- Implement quote editing and user-submitted quotes
- Add quote comments/likes social features
- Support for more languages (i18n)
- Add quote audio playback with text-to-speech
- Implement quote analytics and insights
- Add quote search history and trending quotes

## 👤 Author

**Yogesh Swami**
- GitHub: [ys-pro-duction](https://github.com/ys-pro-duction)
- LinkedIn: [Yogesh sawmi](https://www.linkedin.com/in/yogesh-swamiii)


## 🙏 Acknowledgments

- Supabase team for the excellent backend-as-a-service platform
- Android team for Jetpack Compose
- Open-source community for amazing libraries and tools

---

**Built with ❤️ using AI-powered development**
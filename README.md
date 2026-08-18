# 📚 StudyShare

StudyShare is a native Android application for uploading, organizing, and sharing study materials across courses, backed by a **NoSQL data layer (Cloud Firestore)** with real-time sync, denormalized data for query efficiency, and an event-driven notification pipeline. While the front end is Android/Kotlin, the project's core engineering challenges were **data modeling, query design, and maintaining consistency across a denormalized schema** — skills directly transferable to data analytics and data engineering work.

> Developed as a course project for **MOBDEVE (Mobile Development)** — MCO Group 9.

---

## 🧮 Data Modeling & Engineering Highlights

This is the part of the project most relevant to data-focused roles.

### Schema Design (NoSQL / Document Store)
Designed a 5-collection Firestore schema (`users`, `courses`, `materials`, `subscriptions`, `notifications`) with deliberate trade-offs between normalization and query performance — the same trade-offs you'd weigh designing a dimensional model or a denormalized analytics table:

```
users/{uid}            → firstName, lastName, username, email, createdAt
courses/{courseId}     → courseName, courseAuthor, courseDetails, colorIcon,
                          materialCount, createdAt, updatedAt
materials/{materialId} → materialName, materialType, materialTopic, materialAuthor,
                          fileUrl, fileName, fileSize, courseId, colorIcon, createdAt, updatedAt
subscriptions/{id}     → userId, courseId, createdAt        (many-to-many join table)
notifications/{id}     → userId, courseId, type, materialID, materialName,
                          authorName, isRead, createdAt
```

### Denormalization for Read Efficiency
`materialCount` is stored directly on each `course` document and maintained via atomic `FieldValue.increment()` writes on upload/delete, rather than running a `COUNT` aggregation query on every screen load. This mirrors a common analytics pattern: **pre-aggregating a metric at write time to avoid expensive reads at scale.**

### Fan-Out-on-Write Pipeline
When a material or course changes, the app queries all matching rows in `subscriptions` (`WHERE courseId = X`), then **fans out a write** to `notifications` for every subscribed user except the author. This is a simplified version of the fan-out-on-write pattern used in real notification/feed systems, and it involves reasoning explicitly about the write-amplification vs. read-simplicity trade-off.

### Query Design & Filtering Logic
- Compound queries combining equality filters + ordering (`whereEqualTo` + `orderBy`)
- Set-membership queries: `whereIn` / `whereNotIn` to compute "available courses" as a set difference (all courses − subscribed − authored)
- Prefix-based text search implemented via range queries (`orderBy(name).startAt(text).endAt(text + "\uf8ff")`) — a common workaround for NoSQL stores that lack native full-text search
- Dynamic query rebuilding based on active UI filters (type filter + search text + sort order), analogous to building a query dynamically from user-selected parameters in a BI tool or SQL builder

### Data Consistency & Integrity (without foreign keys)
NoSQL stores don't enforce referential integrity, so integrity had to be handled in application logic:
- Cascading deletes: removing a course also removes all associated `subscriptions`
- Guard conditions: a course can't be deleted while `materialCount > 0`
- Defensive `mapNotNull` / null-coalescing when joining data across collections (since there's no native `JOIN`)

### Real-Time Data Sync
Firestore snapshot listeners (`FirestoreRecyclerAdapter`, `addSnapshotListener`) push live updates to the UI whenever underlying data changes — useful context for anyone who'll later work with streaming data, event-driven pipelines, or real-time dashboards.

### File Metadata Pipeline
A small ETL-style flow: file picked on-device → binary uploaded to **Firebase Storage** → download URL + metadata (`fileName`, `fileSize`, `fileUrl`) written back into the `materials` document — a pattern that generalizes to any workflow separating blob storage from structured metadata (e.g., S3 + a metadata table).

---

## ✨ Application Features

### 🔐 Authentication
- Email/password auth via Firebase Authentication with persistent sessions

### 📂 Courses & Materials
- Create courses with names, descriptions, and color-coded categories
- Upload materials (Notes, Handouts, Reviewers) with file attachments
- Edit/delete with cascading updates to dependent records

### 🔍 Discovery
- Recent uploads feed, keyword search, and type-based filtering
- Subscription management (subscribe/unsubscribe, browse available vs. subscribed courses)

### 🔔 Notifications
- Real-time, per-user notification feed generated from course/material change events
- Read/unread state tracking

### 👤 Profile
- Edit personal info, view authored courses, logout

### 📥 File Handling
- Download materials via Android `DownloadManager` with runtime permission handling

---

## 🛠️ Tech Stack

| Layer            | Technology                                                  |
|-------------------|--------------------------------------------------------------|
| Language          | Kotlin (app logic), Java (data models)                       |
| Data Layer        | Cloud Firestore (NoSQL document store), Firebase Storage      |
| Data Sync         | FirebaseUI `FirestoreRecyclerAdapter`, real-time listeners     |
| Auth              | Firebase Authentication                                       |
| UI                | View Binding, ConstraintLayout, Material Components            |
| Architecture      | Activity + Adapter + ViewHolder, centralized field/key constants |
| Build System      | Gradle (Kotlin DSL), AGP 8.13                                  |
| Min / Target SDK  | API 24 / API 36                                                |

---

## 🏗️ Project Structure

```
app/src/main/java/.../studyshare/
├── models/                     # Data models (Firestore-mapped POJOs)
│   ├── User.java  Course.java  Material.java  Subscription.java  Notification.java
│
├── SplashActivity.kt / LoginActivity.kt / RegisterActivity.kt / AuthRepository.kt
├── MainActivity.kt             # Aggregates subscription + recent-upload queries
├── SearchPageActivity.kt       # Dynamic query builder (filter + search + sort)
├── ManageSubscriptionsActivity.kt  # Set-difference query (available vs subscribed)
├── UploadMaterialActivity.kt   # File → Storage → Firestore metadata pipeline
├── *ViewHolder.kt              # Notification fan-out & cascading delete logic
├── MyFirestoreReferences.kt    # Centralized schema/field constants (single source of truth)
└── IntentKeys.kt
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (Ladybug or newer), JDK 21
- A Firebase project with **Authentication**, **Cloud Firestore**, and **Storage** enabled

### Setup
```bash
git clone <your-repo-url>
cd MOBICOM_STUDYSHARE
```
1. Add your own `google-services.json` to `app/`.
2. Open in Android Studio, let Gradle sync.
3. Build and run (min API 24).

---

## 🎯 Skills Demonstrated

- **Data modeling**: schema design, normalization vs. denormalization trade-offs in a NoSQL context
- **Query design**: compound filters, set-membership (`IN`/`NOT IN`), range-based prefix search, dynamic query construction
- **Data pipelines**: fan-out-on-write for notifications, file-upload metadata pipeline
- **Data integrity**: application-level referential integrity, cascading deletes, guard conditions
- **Real-time systems**: event-driven UI updates from a live data source
- **Software engineering fundamentals**: separation of concerns, centralized constants, clean CRUD operations with error handling

---

## 📄 License

Built for academic purposes as part of a university course.

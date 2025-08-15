The To-Do List App helps you manage your tasks in a simple and organized way. You can create, edit, and delete tasks, set their priority, and easily see what’s most important. All tasks are saved automatically so they remain available even after closing the app.

## Features

- **Add, Edit & Delete Tasks** — Quickly create new tasks, update them, or remove completed ones.  
- **Priority Levels** — Mark tasks as Low, Medium, High, or Urgent for better organization.  
- **Color Codes** — Each priority has its own color so you can spot important work at a glance.  
- **Always Saved** — Your tasks remain even if you close or restart the app.  
- **Easy Navigation** — Clean, dark-mode-friendly design for comfortable use anytime.  

---

## Technical Features

- **Language**: Java  
- **UI**: XML layouts with ConstraintLayout, RecyclerView, and CardView  
- **Storage**:  
  - Persistent local storage (Room Database or SharedPreferences option)  
  - Caching to reduce redundant data loading  
- **Performance**:  
  - Background threading via ExecutorService to avoid UI blocking  
  - Optimized RecyclerView binding to prevent mismatched data when scrolling  
- **State Management**:  
  - Runtime value sharing across activities  
  - Foreground service notification toggle without stopping service  
- **Theme**: Dark mode only with adjusted colors for readability  

---

## Extra Features

- Gradient background that matches status bar & navigation bar.  
- CardView margin adjustments without shrinking size.  
- Rounded icons/images for a modern look.  
- Shape background tinting support.  

---

## Future Scope

- Task search & filter.  
- Due date reminders with notifications.  
- Optional cloud sync for multiple devices.  

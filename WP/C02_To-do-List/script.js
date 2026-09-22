const taskInput = document.getElementById("taskInput");
const addButton = document.getElementById("addButton");
const allButton = document.getElementById("allButton");
const activeButton = document.getElementById("activeButton");
const completedButton = document.getElementById("completedButton");
const taskList = document.getElementById("taskList");

let tasks = JSON.parse(localStorage.getItem("tasks")) || [];
let currentFilter = "all";

function saveTasks() {
    localStorage.setItem("tasks", JSON.stringify(tasks));
}

function renderTasks() {
    taskList.innerHTML = "";

    let filteredTasks;

    if (currentFilter === "completed") {
        filteredTasks = tasks.filter((task) => task.completed);
    } else if (currentFilter === "active") {
        filteredTasks = tasks.filter((task) => !task.completed);
    } else {
        filteredTasks = tasks;
    }

    filteredTasks.forEach((task) => {
        const li = document.createElement("li");

        const taskText = document.createElement("span");
        taskText.textContent = task.text;
        taskText.classList.toggle("completed", task.completed);

        taskText.addEventListener("click", () => {
            task.completed = !task.completed;
            saveTasks();
            renderTasks();
        });

        const deleteButton = document.createElement("button");
        deleteButton.textContent = "Delete";

        deleteButton.addEventListener("click", () => {
            tasks = tasks.filter((item) => item.id !== task.id);
            saveTasks();
            renderTasks();
        });

        li.appendChild(taskText);
        li.appendChild(deleteButton);
        taskList.appendChild(li);
    });
}

function addTask() {
    const text = taskInput.value.trim();

    if (text === "") {
        return;
    }

    tasks.push({
        id: Date.now(),
        text: text,
        completed: false
    });

    saveTasks();
    taskInput.value = "";
    renderTasks();
}

function setActiveFilter(filter) {
    currentFilter = filter;

    allButton.classList.remove("active");
    activeButton.classList.remove("active");
    completedButton.classList.remove("active");

    if (filter === "all") {
        allButton.classList.add("active");
    } else if (filter === "active") {
        activeButton.classList.add("active");
    } else {
        completedButton.classList.add("active");
    }

    renderTasks();
}

addButton.addEventListener("click", addTask);

taskInput.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        addTask();
    }
});

allButton.addEventListener("click", () => {
    setActiveFilter("all");
});

activeButton.addEventListener("click", () => {
    setActiveFilter("active");
});

completedButton.addEventListener("click", () => {
    setActiveFilter("completed");
});

renderTasks();
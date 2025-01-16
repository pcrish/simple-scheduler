window.onload = function() {
    loadData();
};
 var checkSkipAndRerunMethod="";
// function loadData() {
//     document.getElementById('loader').style.display='block'
//     fetch('api/jobs')
//         .then(response => response.json())
//         .then(jobs => {
//             const jobsList = document.getElementById('jobsList');
//             const jobTemplate = document.getElementById('jobsTemplate');
//
//
//             jobs.forEach(job => {
//                 let row$ = jobTemplate.content.querySelector.bind(jobTemplate.content);
//                 row$(".jobId").textContent = job.jobId;
//                 row$(".orderDate").textContent = job.orderDate;
//                 row$(".fullyQualifiedClassName").textContent = job.fullyQualifiedClassName;
//                 row$(".parameter").textContent = job.parameter;
//                 row$(".executableType").textContent = job.executableType;
//                 row$(".name").textContent = job.name;
//                 row$(".description").textContent = job.description;
//                 row$(".tree").textContent = job.tree;
//                 row$(".status").textContent = job.status;
//                 row$(".statusDescription").textContent = job.statusDescription;
//                 row$(".retry").textContent = job.retry;
//                 row$(".sla").textContent = job.sla;
//                 row$(".scheduleType").textContent = job.scheduleType;
//                 row$(".jobDays").textContent = job.jobDays;
//                 let clone = document.importNode(jobTemplate.content, true);
//                 jobsList.appendChild(clone);
//                 //clone.addEventListener('click', showPopup(this));
//             });
//             document.getElementById('loader').style.display='none';
//         });
//
// }

function showPopup() {
    const tr = this.event.target.parentElement;
    document.getElementById('jobId').textContent = tr.getElementsByClassName('jobId')[0].textContent;
    document.getElementById('orderDate').textContent = tr.getElementsByClassName('orderDate')[0].textContent;
    document.getElementById('fullyQualifiedClassName').textContent = tr.getElementsByClassName('fullyQualifiedClassName')[0].textContent;
    let parameter = tr.getElementsByClassName('parameter')[0].textContent;
    if (isJson(parameter)) {
        parameter = JSON.stringify(JSON.parse(parameter), undefined, 2);
    }
    document.getElementById('parameter').textContent = parameter;
    document.getElementById('executableType').textContent = tr.getElementsByClassName('executableType')[0].textContent;
    document.getElementById('name').textContent = tr.getElementsByClassName('name')[0].textContent;
    document.getElementById('description').textContent = tr.getElementsByClassName('description')[0].textContent;
    document.getElementById('tree').textContent = tr.getElementsByClassName('tree')[0].textContent;
    document.getElementById('status').textContent = tr.getElementsByClassName('status')[0].textContent;
    document.getElementById('statusDescription').textContent = tr.getElementsByClassName('statusDescription')[0].textContent;
    document.getElementById('started').textContent = tr.getElementsByClassName('started')[0].textContent;
    document.getElementById('completed').textContent = tr.getElementsByClassName('completed')[0].textContent;
    document.getElementById('error').textContent = tr.getElementsByClassName('error')[0].textContent;
    document.getElementById('retry').textContent = tr.getElementsByClassName('retry')[0].textContent;
    document.getElementById('sla').textContent = tr.getElementsByClassName('sla')[0].textContent;
    document.getElementById('scheduleType').textContent = tr.getElementsByClassName('scheduleType')[0].textContent;
    document.getElementById('jobDays').textContent = tr.getElementsByClassName('jobDays')[0].textContent;
    const myPopup = document.getElementById("myPopup");
    console.log(this.rowIndex)
    myPopup.classList.add("show");
}

function isJson(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}

function fetchJobs(params = {}) {
    document.getElementById('loader').style.display = 'block';

    let url = 'api/jobs?';
    for (const [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${encodeURIComponent(value)}&`;
        }
    }

    url = url.endsWith('&') ? url.slice(0, -1) : url;
    fetch(url)
        .then(response => response.json())
        .then(jobs => {
            const jobsList = document.getElementById('jobsList');
            const jobTemplate = document.getElementById('jobsTemplate');

            jobsList.innerHTML = '';
            jobs.forEach(job => {
                let row$ = jobTemplate.content.querySelector.bind(jobTemplate.content);
                row$(".jobId").textContent = job.jobId;
                row$(".orderDate").textContent = job.orderDate;
                row$(".fullyQualifiedClassName").textContent = job.fullyQualifiedClassName;
                row$(".parameter").textContent = job.parameter;
                row$(".executableType").textContent = job.executableType;
                row$(".name").textContent = job.name;
                row$(".description").textContent = job.description;
                row$(".tree").textContent = job.tree;
                row$(".status").textContent = job.status;
                row$(".statusDescription").textContent = job.statusDescription;
                row$(".started").textContent = job.started;
                row$(".completed").textContent = job.completed;
                row$(".error").textContent = job.error;
                row$(".retry").textContent = job.retry;
                row$(".sla").textContent = job.sla;
                row$(".scheduleType").textContent = job.scheduleType;
                row$(".jobDays").textContent = job.jobDays;

                let clone = document.importNode(jobTemplate.content, true);
                jobsList.appendChild(clone);
            });
        })
        .finally(() => {
            document.getElementById('loader').style.display = 'none';
        });
}

function loadData() {
    fetchJobs();
}

function SearchData() {
    const jobId = document.getElementById("iJobId").value;
    const jobName = document.getElementById("iJobName").value;
    const jobTree = document.getElementById("iJobTree").value;
    const orderDate = document.getElementById("iOrderDate").value;
    const jobStatus = document.getElementById("iJobStatus") ? document.getElementById("iJobStatus").value : '';
    const statusList=getSelectedItems();


    const params = {
        jobId,
        jobName,
        jobTree,
        orderDate,
        jobStatus,
        statusList
    };

    fetchJobs(params);
}
function toggleDropdown() {
    const dropdown = document.querySelector('.dropdown');
    dropdown.classList.toggle('show');
}

window.onclick = function(event) {
    if (!event.target.matches('.dropdown-btn') && !event.target.matches('.dropdown-content') && !event.target.matches('.dropdown-content *')) {
        var dropdowns = document.getElementsByClassName("dropdown");
        for (var i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            openDropdown.classList.remove('show');
        }
    }
}
function getSelectedItems() {
    const selectedItems = [];
    const checkboxes = document.querySelectorAll('.dropdown-content input[type="checkbox"]:checked');
    checkboxes.forEach(checkbox => {
        selectedItems.push(checkbox.id);
    });
    return selectedItems;
}
function openSkipPopup(){
    document.getElementById("message").innerText = "Are you sure want to stop this process?.";
    checkSkipAndRerunMethod="skip";
    openPopUp();
}
function openRerunPopup(){
    document.getElementById("message").innerText = "Are you sure want to rerun this process?.";
    checkSkipAndRerunMethod="rerun";
    openPopUp();
}
function openPopUp(){
    document.getElementById('alertPopup').style.display = 'flex';
}
function closePopupBtn(){
    document.getElementById('alertPopup').style.display = 'none';

}
function rerunOrSkipProcess(){
    if(checkSkipAndRerunMethod=="rerun"){
        alert("rerun process");
    }
    else if(checkSkipAndRerunMethod=="skip") {
        alert("skip process");
    }

}


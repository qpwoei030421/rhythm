function uploadFile() {
    // CSRF 토큰 및 헤더 정보 가져오기
    const header = document.querySelector('meta[name="_csrf_header"]').content;
    const token = document.querySelector('meta[name="_csrf"]').content;

    const fileInput = document.getElementById("fileInput");
    if (fileInput.files.length === 0) {
        alert("파일을 선택해주세요.");
        return;
    }

    const file = fileInput.files[0];
    const formData = new FormData();
    formData.append("file", file);

    fetch("http://localhost:8080/audioUpload", {
        method: "POST",
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'X-CSRF-Token': token
        },
        body: formData
    })
        .then(response => response.text())
        .then(data => {
            document.getElementById("uploadStatus").innerText = "업로드 성공: " + data;
        })
        .catch(error => {
            console.error("업로드 실패:", error);
            document.getElementById("uploadStatus").innerText = "업로드 실패";
        });
}

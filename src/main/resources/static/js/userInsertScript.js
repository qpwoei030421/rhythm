function signupCheck() {

    if (!document.getElementById('username').value) {
        alert('이메일을 입력해 주세요.');

        return false;
    }

    if (!document.getElementById('userPw').value) {
        alert('비밀번호를 입력해 주세요.');

        return false;
    }

    if (!document.getElementById('userNick').value) {
        alert('닉네임을 입력해 주세요.');

        return false;
    }

    if (document.getElementById('userPw').value !== document.getElementById('repeatUserPw').value) {
        alert('비밀번호가 일치하지 않습니다.');

        return false;
    }

    if (document.getElementById("isIdOverlappingChecked").value !== "T") {
        alert("계정 중복 확인이 필요합니다.");

        return false;
    }

    return true;
}

function emailOverlap() {
    // CSRF 토큰 및 헤더 정보 가져오기
    const header = document.querySelector('meta[name="_csrf_header"]').content;
    const token = document.querySelector('meta[name="_csrf"]').content;

    const username = document.getElementById("username").value;

    if (!username) {
        alert('이메일을 입력하세요.');
        return;
    }

    // fetch 요청
    fetch('/user/emailOverlappingCheck', {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
            'X-CSRF-Token': token
        },
        body: JSON.stringify({ username }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.authCode == 'success') {
                alert('사용 가능한 이메일입니다.');
                document.getElementById("isEmailOverlappingChecked").value = "T";
            } else {
                alert('이미 사용중인 이메일입니다.');;
            }
        })
        .catch(error => {
            console.error('Fetch error:', error);
            alert('중복 확인 중 오류가 발생했습니다.');
        });
}

function checkPw() {
    const pwInput = document.getElementById('userPw').value;
    const pwMessage = document.getElementById('pwMessage');
    const hasLetter = /[A-Za-z]/.test(pwInput); // 영문 포함 여부
    const hasDigit = /[0-9]/.test(pwInput); // 숫자 포함 여부
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(pwInput); // 특수 문자 포함 여부

    if (!pwInput) {
        pwMessage.textContent = "비밀번호를 입력해 주세요.";
        pwMessage.className = "error";
        return false;
    } else if (pwInput.length < 8 || pwInput.length > 32 || !(hasLetter && hasDigit && hasSpecial)) {
        pwMessage.textContent = "비밀번호는 8 ~ 32자이며, 영문, 숫자, 특수 문자를 포함해야 합니다.";
        pwMessage.className = "error";
        return false;
    } else {
        pwMessage.textContent = "유효한 비밀번호입니다.";
        pwMessage.className = "valid";
        return true;
    }
}

function checkPw2() {
    if (document.getElementById("userPw").value !== document.getElementById("repeatUserPw").value) {
        pw2Message.textContent = "비밀번호가 일치하지 않습니다.";
        pw2Message.className = "error";
        return false;
    } else {
        pw2Message.textContent = null;
        pw2Message.className = null;
        return true;
    }
}

// Matter.js 모듈 불러오기
const { Engine, Render, Runner, World, Bodies, Events } = Matter;

// 엔진과 월드 생성
const engine = Engine.create();
const world = engine.world;

// 캔버스에 렌더링 설정
const render = Render.create({
    element: gameContainer,
    engine: engine,
    options: {
        width: 800,
        height: 600,
        wireframes: false // 실제 그래픽 적용
    }
});

Render.run(render);
const runner = Runner.create();
Runner.run(runner, engine);

// CSRF 토큰 및 헤더 정보 가져오기
const header = document.querySelector('meta[name="_csrf_header"]').content;
const token = document.querySelector('meta[name="_csrf"]').content;

// 사용자 점수 관련 변수
let userScore = 0;
let userCombo = 0;
let userMaxCombo = 0;
let displayType = "";
let displayTypeTimmer = 0;

// 소리 효과음
const soundEffect = new Audio('../sound/Slap Face weak.mp3');

function playSoundEffect() {
    const clone = soundEffect.cloneNode();
    clone.currentTime = 0; // 소리를 처음부터 재생
    clone.play();
}

// Queue 로직 클래스
class Queue {
    constructor() {
        this.items = [];
    }

    enqueue(item) {  // 큐에 추가
        this.items.push(item);
    }

    dequeue() {  // 큐에서 제거 (가장 먼저 들어온 요소)
        return this.items.shift();
    }

    front() {  // 큐의 맨 앞 요소 확인
        return this.items[0];
    }

    isEmpty() {  // 큐가 비었는지 확인
        return this.items.length === 0;
    }

    size() {  // 큐의 크기 확인
        return this.items.length;
    }
}

// 각 키별 Queue 선언
const noteQueues = {
    "d": new Queue(),
    "f": new Queue(),
    "j": new Queue(),
    "k": new Queue()
};

// 중력 설정
engine.world.gravity.y = 0;

// 사용자 입력 처리
window.addEventListener("keydown", (event) => {
    const key = event.key.toLowerCase();    // 입력키를 소문자로 변환
    // 게임 입력 처리
    if (key === 'd' || key === 'f' || key === 'j' || key === 'k') { // d,f,j,k 키중 하나를 입력했을 때
        playSoundEffect();
        if (noteQueues[key]) {
            if (!noteQueues[key].isEmpty()) {
                const frontNote = noteQueues[key].front();
                if (frontNote.position.y >= 490 && frontNote.position.y <= 510) {
                    World.remove(world, frontNote);
                    noteQueues[key].dequeue();
                    console.log("PERFECT!");
                    updateScore("PERFECT");
                } else if (frontNote.position.y >= 475 && frontNote.position.y <= 525) {
                    World.remove(world, frontNote);
                    noteQueues[key].dequeue();
                    console.log("GREAT!");
                    updateScore("GREAT");
                } else if (frontNote.position.y >= 450 && frontNote.position.y <= 550) {
                    World.remove(world, frontNote);
                    noteQueues[key].dequeue();
                    console.log("GOOD");
                    updateScore("GOOD");
                } else {
                    World.remove(world, frontNote);
                    noteQueues[key].dequeue();
                    console.log("BAD");
                    updateScore("BAD");
                }
            }
        }
    }
});

// 페이지 로딩 후, 게임 시작 버튼을 클릭 시 음악을 재생하도록 처리 -> 자동 재생 정책
const startButton = document.getElementById('startButton');

// 음악 이름 가져오기
const urlParams = new URLSearchParams(window.location.search);
const musicTitle = urlParams.get('musicTitle');
let countDown;

startButton.addEventListener('click', () => {

    countDown = Date.now();

    // 버튼 클릭 후 4초의 시간을 줌
    setTimeout(() => {

        // 음악 데이터 가져오기
        fetch("http://localhost:8080/api/beats?musicTitle=" + encodeURIComponent(musicTitle), {
            method: "GET",
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-Token': token
            }
        })
            .then(response => response.json())
            .then(beats => {
                // console.log("받은 비트 데이터:", beats);
                beats.forEach(beat => {
                    console.log(`Id: ${beat.id}, Time: ${beat.timeStamp}, D: ${beat.d}, F: ${beat.f}, J: ${beat.j}, K: ${beat.k}`);
                });

                // timeStamp 순으로 정렬
                beats.sort((a, b) => a.timeStamp - b.timeStamp);

                // 노트 생성 시작 시간
                const startTime = Date.now();

                // 음악 시작
                setTimeout(() => {
                    const audio = new Audio("http://localhost:8080/api/music?musicTitle=" + encodeURIComponent(musicTitle));
                    audio.volume = 0.5;
                    audio.play();
                }, 650);

                // 비트 생성 함수 호출
                beats.forEach((beat, index) => {
                    const delay = (beat.timeStamp - beats[0].timeStamp) * 1000;
                    const isLast = index === beats.length - 1;

                    setTimeout(() => {
                        const elapsed = Date.now() - startTime; // 현재 경과 시간
                        console.log(`노트 생성: ${beat.timeStamp}, 현재 경과: ${elapsed / 1000}`);

                        if (beat.d) createNote(beat.timeStamp, 'd');
                        if (beat.f) createNote(beat.timeStamp, 'f');
                        if (beat.j) createNote(beat.timeStamp, 'j');
                        if (beat.k) createNote(beat.timeStamp, 'k');

                        if (isLast) {
                            gameOver();
                        }
                    }, delay);
                });
            })
            .catch(error => console.error("Error fetching beats:", error));
    }, 4000);

});

const encodedMusicTitle = encodeURIComponent(musicTitle);
// 게임 종료 함수
function gameOver() {
    setTimeout(() => {
        window.location.href = `/rhythm/gameOver?score=${userScore}&userMaxCombo=${userMaxCombo}&musicTitle=${encodedMusicTitle}`;
    }, 1000);
}

// 노트 생성 함수
function createNote(timeStamp, key) {
    let xPosition;  // key에 따라 x위치 조정

    switch (key) {
        case 'd': xPosition = 175; break;
        case 'f': xPosition = 325; break;
        case 'j': xPosition = 475; break;
        case 'k': xPosition = 625; break;
    }

    const note = Bodies.rectangle(xPosition, 100, 100, 20, {
        restitution: 0, // 반발력
        friction: 0,    // 마찰력
        render: {
            fillStyle: "red" // 노트 색상
        },
        collisionFilter: {
            group: -1 // 서로 다른 노트들이 충돌하지 않도록 설정
        }
    });
    World.add(world, note);

    // 노트 속도 설정
    Matter.Body.setVelocity(note, { x: 0, y: 10 });

    // 노트를 Queue에 저장
    noteQueues[key].enqueue(note);

    // 10ms 마다 노트의 위치 출력
    //setInterval(() => {
    //    console.log(note.position.y);
    //}, 10);

    return note;
}

// 버튼 생성 함수
function createRect(x, y, width, height) {
    const rect = Bodies.rectangle(x, y, width, height, {
        isStatic: true,
        isSensor: true,
        render: {
            fillStyle: "blue" // 직사각형 색상
        }
    });
    World.add(world, rect);
}

// 버튼 생성 (x, y, width, height)
createRect(175, 490, 100, 20); // d
createRect(325, 490, 100, 20); // f
createRect(475, 490, 100, 20); // j
createRect(625, 490, 100, 20); // k

// 점수 표시용 Matter.js 객체 생성
const scoreDisplay = Bodies.rectangle(400, 50, 200, 50, {
    isStatic: true,
    isSensor: true,
    render: {
        fillStyle: "black",
        strokeStyle: "white",
        lineWidth: 3
    }
});
World.add(world, scoreDisplay);

// 콤보 표시용 Matter.js 객체 생성
const comboDisplay = Bodies.rectangle(400, 100, 200, 50, {
    isStatic: true,
    isSensor: true,
    render: {
        fillStyle: "black",
        strokeStyle: "white",
        lineWidth: 3
    }
});
World.add(world, comboDisplay);

// 점수 & 콤보 업데이트 함수
function updateScore(type) {
    if (type === "PERFECT") userScore += 100;
    else if (type === "GREAT") userScore += 80;
    else if (type === "GOOD") userScore += 50;

    userCombo = (type === "BAD" || type === "GOOD" || type === "MISS") ? 0 : userCombo + 1;

    if (userCombo > userMaxCombo) userMaxCombo = userCombo;

    // type 표시
    displayType = type;
    displayTypeTimmer = Date.now() + 1000; // 1000ms 초뒤에 텍스트 사라짐
}

// 점수 & 콤보 업데이트 표기 함수
function updateScoreDisplay() {
    scoreDisplay.render.text = `Score: ${userScore}`;
    comboDisplay.render.text = `Combo: ${userCombo}`;
}


// afterUpdate
Events.on(engine, 'afterUpdate', () => {
    //console.log("노트 위치:", note.position);

    Object.keys(noteQueues).forEach(key => {
        if (!noteQueues[key].isEmpty()) {
            const frontNote = noteQueues[key].front();
            if (frontNote.position.y >= 550) {
                World.remove(world, frontNote);
                noteQueues[key].dequeue();
                updateScore("MISS");
                console.log(`MISS (${key.toUpperCase()})`);
            }
        }
    });
});

// 텍스트 렌더링
Events.on(render, "afterRender", (event) => {
    const ctx = render.context;

    // d,f,j,k 키 가이드
    ctx.font = "16px Arial";
    ctx.fillStyle = "white";
    ctx.fillText("D", 175, 497);
    ctx.fillText("F", 325, 497);
    ctx.fillText("J", 475, 497);
    ctx.fillText("K", 625, 497);

    // score, combo 출력
    ctx.font = "24px Arial";
    ctx.fillStyle = "white";
    ctx.textAlign = "center";

    ctx.fillText(`Score: ${userScore}`, 400, 55);  // 점수 출력
    ctx.fillText(`Combo: ${userCombo}`, 400, 105); // 콤보 출력

    // type 출력
    if (Date.now() < displayTypeTimmer) {
        console.log(Date.now);
        console.log(displayTypeTimmer);
        ctx.font = "32px Arial bold";

        // type에 따른 색상 변경
        if (displayType === "PERFECT") ctx.fillStyle = "yellow";
        if (displayType === "GREAT") ctx.fillStyle = "blue";
        if (displayType === "GOOD") ctx.fillStyle = "green";
        if (displayType === "BAD" || displayType === "MISS") ctx.fillStyle = "gray";

        ctx.fillText(displayType, 400, 200);
    }

    // 카운트 다운 출력
    if (Date.now() <= countDown + 4000) {
        let cnt = '0';
        ctx.font = "48px Arial bold";
        ctx.fillStyle = "yellow";

        if (Date.now() >= countDown && Date.now() <= countDown + 1000) cnt = '3';
        if (Date.now() >= countDown + 1000 && Date.now() <= countDown + 2000) cnt = '2';
        if (Date.now() >= countDown + 2000 && Date.now() <= countDown + 3000) cnt = '1';
        if (Date.now() >= countDown + 3000 && Date.now() <= countDown + 4000) cnt = 'START!';

        ctx.fillText(cnt, 400, 200);
    }
});

window.onload = () => {

};

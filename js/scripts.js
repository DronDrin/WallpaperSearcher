let first, game, gameWidth, gameHeight, lose, win;
let mines = 99;
let minWidth = 0;
let minutes, seconds;
$(document).bind('refresh', e => {
    if (first)
        return;
    checkMines();
    const field = game.getPlayerField();
    for (let i = 0; i < field.length; i++) {
        for (let j = 0; j < field[i].length; j++) {
            const sqr = $('.game__item__' + i + '_' + j);
            if (field[i][j] == -2) {
                if (!sqr.hasClass('game__item__minned'))
                    sqr.addClass('game__item__minned');
                continue;
            }
            else {
                if (sqr.hasClass('game__item__minned'))
                    sqr.removeClass('game__item__minned');
            }
            if (field[i][j] == -3) {
                if (!sqr.hasClass('game__item__error'))
                    sqr.addClass('game__item__error');
                continue;
            }
            if (field[i][j] == 0) {
                if (!sqr.hasClass('game__item__void'))
                    sqr.addClass('game__item__void');
                continue;
            }
            else {
                if (sqr.hasClass('game__item__void'))
                    sqr.removeClass('game__item__void');
            }
            if (field[i][j] > 0 && field[i][j] < 9) {
                if (!sqr.hasClass('game__item__' + field[i][j]))
                    sqr.addClass('game__item__' + field[i][j]);
                sqr.children().html(field[i][j]);
                sqr.children().css({ 'opacity': '1' });
            }
            else if (field[i][j] == 9) {
                sqr.addClass('game__item__losed');
            }
            else if (field[i][j] == 10) {
                sqr.addClass('game__item__losed-click');
            }
        }
    }
});
$(document).bind('lose', e => {
    lose = true;
    showEndModule('Вы проиграли!');
});
$(document).bind('win', e => {
    win = true;
    showEndModule('Вы победили!');
});
$('.endModule__newGame').click(newGame);
$('.menu__burger').click(e => {
    if ($(e.target).parent().hasClass('menu__open'))
        $(e.target).parent().removeClass('menu__open');
    else
        $(e.target).parent().addClass('menu__open');
});
$('.menu__body-item__inmenu').click(inMenu);
$('.menu__body-item__newgame').click(newGame);
function newGame() {
    location.reload();
}
function inMenu() {
    location.reload();
}
function createGame(width, height) {
    $('.mines-counter__counter').html(mines);
    win = false;
    lose = false;
    gameWidth = width;
    gameHeight = height;
    first = true;
    let gameHTML = '';
    for (let i = 0; i < width; i++) {
        let gameColumn = '<div class="game__column">';
        for (let j = 0; j < height; j++)
            gameColumn += '<div class="game__item game__item__' + i + '_' + j +
                '" onmousedown="clickSquare(event)" oncontextmenu="return false;"><div>0</div></div>';
        gameColumn += '</div>';
        gameHTML += gameColumn;
    }
    $('#game').append(gameHTML);
    updateSquares();
}
function selectMode(e) {
    if (e.target.id == 'easy') {
        mines = 10;
        createGame(10, 10);
        $('.game-app').show();
    }
    else if (e.target.id == 'medium') {
        mines = 40;
        createGame(16, 16);
        $('.game-app').show();
    }
    else if (e.target.id == 'hard') {
        mines = 99;
        createGame(30, 16);
        $('.game-app').show();
    }
    else if (e.target.id == 'create') {
        $('.create-out').show();
    }
    else if (e.target.id == 'createButton') {
        mines = $('.create__mines').val();
        createGame($('.create__width').val(), $('.create__height').val());
        $('.game-app').show();
        $('.create-out').hide();
    }
    checkFontSize();
    $('.mode-app').hide();
}

function updateSquares() {
    let squares = $('.game__item');
    let val;
    if (0.6 * $(window).width() / gameWidth < 0.6 * $(window).height() / gameHeight)
        val = 0.6 * $(window).width() / gameWidth;
    else
        val = 0.6 * $(window).height() / gameHeight;
    for (let i = 0; i < squares.length; i++) {
        $(squares[i]).width(val);
        $(squares[i]).height(val);
    }
}

function createGameOnClick(x, y) {
    first = false;
    game = new Game();
    game.createGame(gameWidth, gameHeight, mines, Number.parseInt(x), Number.parseInt(y));
    seconds = minutes = 0;
    setTimeout(timeTimer, 1000);
    let opened = game.openArea(Number.parseInt(x), Number.parseInt(y));
    game.refresh();
    return opened;
}

function clickSquare(e) {
    if (lose || win)
        return;
    let match = /game__item__(\d+)_(\d+)/.exec(e.target.classList[1]);
    const x = match[1];
    const y = match[2];
    if (first) {
        if (e.button == 0) {
            createGameOnClick(x, y);
        }
    }
    else {
        if (e.button == 0)
            game.openSquare(Number.parseInt(x), Number.parseInt(y));
        else
            game.setMine(Number.parseInt(x), Number.parseInt(y));
    }
    return false;
}

function timeTimer() {
    if (win || lose)
        return;
    seconds++;
    if (seconds >= 60) {
        seconds = 0;
        minutes++;
    }
    let time = '';
    if (minutes < 10)
        time += '0';
    time += minutes;
    time += ':';
    if (seconds < 10)
        time += '0';
    time += seconds;
    $('.time-counter__counter').html(time);
    setTimeout(timeTimer, 1000)
}

function checkMines() {
    let counter = mines;
    const field = game.getPlayerField();
    for (let i = 0; i < field.length; i++) {
        for (let j = 0; j < field[i].length; j++) {
            if (field[i][j] == -2)
                counter--;
        }
    }
    $('.mines-counter__counter').html(counter);
}

function checkFontSize() {
    let items = $('.game__item');
    for (let i = 0; i < items.length; i++) {
        $(items[i]).children().css({ 'font-size': ($(items[i]).height() + 7) + 'px' });
    }
}


$(window).resize(() => {
    updateSquares();
    checkFontSize();
});

function closeEndModule() {
    $('.endModule').css({ 'transform': 'translate(-100%, -50%)' });
}
function showEndModule(text) {
    $('.endModule').css({ 'transform': 'translate(0%, -50%)' });
    $('.endModule__text').html(text);
}


document.querySelector('.inputer').onkeydown = e => {
    if (e.keyCode === 13) {
        let value = document.querySelector('.inputer').value;

        let mark = value.split(" ")[0];
        let second = value.split(" ")[1];
        console.log(value);
        let x = Number.parseInt(second.split("-")[0]);
        let y = Number.parseInt(second.split("-")[1]);

        let opened;
        if (mark === 's') {
            if (first) {
                opened = createGameOnClick(x, y)
            } else {
                opened = game.openSquare(x, y);
            }
            opened = Array.from(new Set(opened));
            if (opened.length > 1) {
                document.querySelector('.inputer').value =
                    game.field[x][y] + ";" + opened.map(c => c.x + "-" + c.y + "-" + game.field[c.x][c.y])
                        .join(";");
            } else {
                document.querySelector('.inputer').value = game.field[x][y];
            }
        } else {
            game.setMine(x, y);
            document.querySelector('.inputer').value = "";
        }
    }
};
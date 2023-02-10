/* значения для fieldPlayer:
 * -1 - закрытое поле
 * -2 - мина поставленная игроком
 * 0 - 8 - кол-во мин в этой клетке
 * 9 - мина
 */
class Game {
    constructor() {
        this.field = [];
        this.gameWidth = 0;
        this.gameHeight = 0;
        this.fieldPlayer = [];
        this.losed = false;
    }

    getField() {
        return this.field;
    }

    getPlayerField() {
        return this.fieldPlayer;
    }

    createGame(width, height, mines, xArea, yArea) {
        this.gameWidth = width;
        this.gameHeight = height;
        this.field = generateFieldArea(width, height, mines, xArea, yArea);
        this.fieldPlayer = [];
        for (let i = 0; i < width; ++i) {
            this.fieldPlayer[i] = [];
            for (let j = 0; j < height; ++j) {
                this.fieldPlayer[i][j] = -1;
            }
        }
        this.refresh();
    }

    openSquare(x, y) {
        if (this.losed)
            return [];
        let opened = this.openSquareNoCheckWin(x, y);
        this.refresh();
        for (let i = 0; i < this.fieldPlayer.length; i++) {
            for (let j = 0; j < this.fieldPlayer[i].length; j++) {
                if (this.fieldPlayer[i][j] == -1 && this.field[i][j] != 9) {
                    return opened;
                }
            }
        }
        this.win();
        return opened;
    }

    openSquareNoCheckWin(x, y) {
        let opened = [];
        opened.push({x, y});
        if (this.fieldPlayer[x][y] == -1) {
            if (this.field[x][y] == 0) {
                let openAreaOpened = this.openArea(x, y);
                for (const openAreaOpenedElement of openAreaOpened) {
                    opened.push(openAreaOpenedElement);
                }
            }
            else if (this.field[x][y] == 9)
                this.lose(x, y);
            else
                this.fieldPlayer[x][y] = this.field[x][y];
        }
        return opened;
    }

    refresh() {
        jQuery.event.trigger('refresh');
    }

    openArea(x, y) {
        let opened = [];
        this.fieldPlayer[x][y] = this.field[x][y];
        if (y > 0) {
            Array.prototype.push.apply(opened, this.openSquareNoCheckWin(x, y - 1));
            if (x < this.gameWidth - 1) Array.prototype.push.apply(opened, this.openSquareNoCheckWin(x + 1, y - 1));
            if (x > 0) Array.prototype.push.apply(opened, this.openSquareNoCheckWin(x - 1, y - 1));
        }

        if (y < this.gameHeight - 1) {
            Array.prototype.push.apply(opened, this.openSquareNoCheckWin(x, y + 1));
            if (x < this.gameWidth - 1) Array.prototype.push.apply(opened, this.openSquareNoCheckWin(x + 1, y + 1));
            if (x > 0) Array.prototype.push.apply(opened, this.openSquareNoCheckWin(x - 1, y + 1));
        }

        if (x > 0) Array.prototype.push.apply(opened, this.openSquareNoCheckWin(x - 1, y));
        if (x < this.gameWidth - 1) Array.prototype.push.apply(opened, this.openSquareNoCheckWin(x + 1, y));
        return opened;
    }

    lose(x, y) {
        this.losed = true;
        for (let i = 0; i < this.fieldPlayer.length; i++) {
            for (let j = 0; j < this.fieldPlayer[i].length; j++) {
                if (this.fieldPlayer[i][j] == -2 && this.field[i][j] != 9)
                    this.fieldPlayer[i][j] = -3;
                if (this.field[i][j] == 9) {
                    if (this.fieldPlayer[i][j] != -2)
                        this.fieldPlayer[i][j] = 9;
                }
            }
        }
        this.fieldPlayer[x][y] = 10;
        jQuery.event.trigger('lose', [x, y]);
        jQuery.event.trigger('refresh');
    }

    win() {
        if (this.losed)
            return;
        jQuery.event.trigger('win', []);
    }

    setMine(x, y) {
        if (this.fieldPlayer[x][y] == -2)
            this.fieldPlayer[x][y] = -1;
        else if (this.fieldPlayer[x][y] == -1)
            this.fieldPlayer[x][y] = -2;
        this.refresh();
    }
}


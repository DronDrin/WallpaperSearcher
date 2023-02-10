function generateField(width, height, mines) {
    return setNumbers(setMines(getClearField(width, height), mines));
}

function generateFieldArea(width, height, mines, xArea, yArea) {
    let field;
    do {
        field = generateField(width, height, mines);
    } while (field[xArea][yArea] != 0);
    return field;
}

function getClearField(width, height) {
    let field = [];
    for (let i = 0; i < width; ++i) {
        field[i] = [];
        for (let j = 0; j < height; ++j) {
            field[i][j] = 0;
        }
    }
    return field;
}

function setMines(field, mines) {
    for (let i = 0; i < mines; i++) {
        let x, y;
        do {
            x = getRandomInt(field.length);
            y = getRandomInt(field[0].length);
        } while (field[x][y] == 9);
        field[x][y] = 9;
    }
    return field;
}

function setNumbers(field) {
    for (let i = 0; i < field.length; ++i) {
        for (let j = 0; j < field[i].length; ++j) {
            if (!(field[i][j] == 9)) {
                if (i > 0) {
                    if (field[i - 1][j] == 9) // верх
                        field[i][j]++;
                    if (j > 0 && field[i - 1][j - 1] == 9) // верх - лево
                        field[i][j]++;
                    if (j < field[i].length - 1 && field[i - 1][j + 1] == 9) // верх - право
                        field[i][j]++;
                }

                if (i < field.length - 1) {
                    if (field[i + 1][j] == 9) // низ
                        field[i][j]++;
                    if (j > 0 && field[i + 1][j - 1] == 9) // низ - лево
                        field[i][j]++;
                    if (j < field[i].length - 1 && field[i + 1][j + 1] == 9) // низ - право
                        field[i][j]++;
                }

                if (j > 0 && field[i][j - 1] == 9)  // лево
                    field[i][j]++;
                if (j < field[i].length - 1 && field[i][j + 1] == 9)  // право
                    field[i][j]++;
            }
        }
    }
    return field;
}



function getRandomInt(max) {
    return Math.floor(Math.random() * max);
}

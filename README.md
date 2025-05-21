<h1>Wallpaper searcher</h1>  
<h3>Download wallpapers from wallhaven.cc</h3>  

---

<h2>Dependencies:</h2>  
Program is made in java (actually in groovy, but it's compiled to .class or to .jar).

So you need java - openjre 17.  
How to install java?  
For example:  

**Arch linux**  
`pacman -S jdk17-openjdk`  
  
**Ubuntu**  
`apt install openjdk-17-jre`

---

<h2>How to download</h2>  
Go to https://github.com/DronDrin/WallpaperSearcher, select last release, and just download it.

But if you want to make your own .jar file, you can clone repository https://github.com/DronDrin/WallpaperSearcher,  
and run this command:  
`gradle jar`  
Of course, you must have gradle installed on your system.  
So that's all! Your .jar is located at ROOT_DIRECTORY/build/libs/.  

---

<h2>How to run:</h2>
Program is .jar file.  
So to start it you should enter this command:  
`java -jar PATH_TO_JAR`  

---
<h2>Help:</h2>  
<h4>help</h4>  

this text is shown at first start of program  
also run program with -h or --help parameter to see this text  

<h4>parameters</h4>
There are some parameters:

  * '-q', '--query': :phrase that used to search wallpapers  
  * '-l', '--at-least': :minimum resolution of wallpaper  
  * '-r', '--ratio': :needed ratio of wallpaper  
  * '-p', '--path': :directory to save wallpapers in. Parent of this directory must exist  
  * '-n', '--number-of-packages': :Wallpapers is downloaded by batches (packages). Size of one package - 24.  
(Why? Because wallhaven.cc set this value as max query size)

<h4>modes</h4>
The program has two modes:  
  * first - interactive:  
    * program asks you for each parameter  
    * start program with '-i' parameter to use this mode  
  * second - parameterized:  
    * use parameters from second paragraph, for example:  
    * -q nature -l 1920x1080 -r 16x9 -p /home/alex/Wallpapers -n 5  

<h4>caching</h4>

agree, it would not be good if you would have to enter each parameter again every time when
you start the program. So each time when you enter any parameter, the program saves it.
Next time, you can skip this parameter (in all modes), and the program will use saved data.
But if you have never entered skipped parameter before, there will an error, so be careful with skipping.
<h4>example</h4>
here is an example:

error ---- image 4djkeo has wrong resolution(1366x768)! It will not be downloaded  
ok ------- loading file y89yyg.jpeg to /home/alex/Downloads/wallpapers/y89yyg.jpeg  
ok ------- loading file kwx5x7.jpeg to /home/alex/Downloads/wallpapers/kwx5x7.jpeg  
ok ------- loading file 8odql1.jpeg to /home/alex/Downloads/wallpapers/8odql1.jpeg  
error ---- java.io.IOException: File y89yyg.jpeg already exist!  
error ---- image 2kdemx has wrong resolution(1366x768)! It will not be downloaded  
ok ------- loading file dg8ddj.jpeg to /home/alex/Downloads/wallpapers/dg8ddj.jpeg  
error ---- java.io.IOException: File dg8ddj.jpeg already exist!  
ok ------- loading file zx19qy.jpeg to /home/alex/Downloads/wallpapers/zx19qy.jpeg  
  ------------ finished 7/24 (2 errors) +++++--=================  
  
last line is a progress bar.  
finished 'loaded or can't be loaded wallpapers'/24 ('number of errors' error(s)) ++++---==========  
Number of pluses means number of successfully downloaded wallpapers  
Number of minuses means number of wallpapers can't be loaded.  
Number of equals signs means number of wallpapers that are loading yet.  
  
message 'File xxxxxx.xxx already exist!' means you already have that file.  
message 'has wrong resolution' means that wallpaper has too small resolution or wrong ratio  
(for example, you selected ratio 16x9, but wallpaper has 21x9)  

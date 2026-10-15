@echo off
echo ==========================================================
echo Running Member 3 Verification Test Suite
echo ==========================================================
javac -cp "lib/*;bin;." -d bin (Get-ChildItem -Path src,test -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp "lib/*;bin" test.Member3VerificationTest
pause

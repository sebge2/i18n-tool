const fs = require('fs');
const { execSync } = require('child_process');

function getUserRootFolder() {
  return process.env.HOME || process.env.HOMEPATH || process.env.USERPROFILE;
}

export function execCmd(command) {
  execSync(command, {
    stdio: 'inherit',
  });
}

function isDirectory(pathItem) {
  try {
    return fs.statSync(pathItem).isDirectory();
  } catch (e) {
    return false;
  }
}

function deleteDirectory(path) {
  if( fs.existsSync(path) ) {
    fs.readdirSync(path).forEach(function(file,index){
      const curPath = path + "/" + file;

      if(fs.lstatSync(curPath).isDirectory()) { // recurse
        deleteDirectory(curPath);
      } else { // delete file
        fs.unlinkSync(curPath);
      }
    });
    fs.rmdirSync(path);
  }
}

const swaggerGroupId = 'io.swagger.codegen.v3';
const swaggerArtifactId = 'swagger-codegen-cli';
const swaggerVersion = '3.0.20';
const swaggerLocation = `${getUserRootFolder()}/.m2/repository/${swaggerGroupId.replace(
    /\./g,
  '/',
)}/${swaggerArtifactId}/${swaggerVersion}/${swaggerArtifactId}-${swaggerVersion}.jar`;
const apiUrl = process.env.API_URL;
const angularVersion = '8';
const sourceLocation = '../web/src/app/api';

if (isDirectory(sourceLocation)) {
  console.log(`[i18n tool] Deleting source directory ${sourceLocation}.`);

  deleteDirectory(sourceLocation);
}

console.log(`[i18n tool] Downloading Swagger Code Generator if Needed.`);
execCmd(
  `mvn dependency:get -DgroupId=${swaggerGroupId} -DartifactId=${swaggerArtifactId} -Dversion=${swaggerVersion}`,
);

console.log(`[i18n tool] Generating Model.`);
execCmd(
  `java -classpath ${swaggerLocation} io.swagger.codegen.v3.cli.SwaggerCodegen generate -i ${apiUrl} -l typescript-angular --model-name-suffix 'Dto' --additional-properties ngVersion=${angularVersion} -o ${sourceLocation} -a "Authorization:Basic YWRtaW46YWRtaW4="`,
);

console.log(`[i18n tool] Model Generated.`);

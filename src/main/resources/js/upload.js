/**
 * Created by dominik on 21.01.17.
 */
AJS.toInit(function () {
    var file = undefined;

    AJS.$("#post-inputs").click(function () {
        console.log("about to post inputs");
        file = document.getElementById("upload-field").value;
        if(file === undefined) {
            AJS.messages.error({
                title: "Error !",
                body: "Select a Hardware Backup Zip-File to proceed!"
            })
        }
        else {
            if(!file.includes(".zip")){
                AJS.messages.error({
                    title: "Error !",
                    body: "The Datatype of the Backup must be .zip!"
                })
            }
            else {
                AJS.$("#upload-form").submit();
            }
        }
    })
});

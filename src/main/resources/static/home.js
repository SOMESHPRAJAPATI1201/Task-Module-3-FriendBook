document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("addPostForm");

  form.addEventListener("submit", function (event) {
    event.preventDefault(); // Prevent default form submission

    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      return; // Stop if form validation fails
    }

    const formData = new FormData();

    // Ensure you're getting the correct input for post content
    const postContent = document.getElementById("postContent").value;

    // Create a post object with userId nested inside a user object
    const post = {
      postName: postContent,
      user: {
        userId: document.getElementById("userId").value, // Get userId from hidden input
      },
    };

    // Append the post object as a JSON blob
    formData.append(
      "post",
      new Blob([JSON.stringify(post)], { type: "application/json" })
    );

    // Get the post image file
    const postImage = document.getElementById("postImage").files[0];
    if (postImage) {
      formData.append("postImage", postImage);
    }

    fetch("/addPost", {
      method: "POST",
      body: formData,
      headers: {
        Accept: "text/html", // Request HTML response if desired
      },
    })
      .then((response) => {
        if (response.ok) {
          const contentType = response.headers.get("content-type");
          if (contentType && contentType.includes("text/html")) {
            // If HTML, redirect to that page
            return response.text().then((html) => {
              document.open();
              document.write(html);
              document.close();
            });
          } else {
            // If JSON, handle the response
            return response.json().then((data) => {
              console.log("Post uploaded successfully:", data);
              alert("Post added successfully!");
            });
          }
        } else {
          throw new Error("Failed to add post");
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("An error occurred while adding the post.");
      });
  });
});

///Like
 function attachLikeFormListeners() {
   const forms = document.querySelectorAll(".likePostForm");
   forms.forEach((form) => {
     form.addEventListener("submit", function (event) {
       event.preventDefault(); // Prevent default form submission

       if (!form.checkValidity()) {
         form.classList.add("was-validated");
         return;
       }

       const posts = {
         postId: parseInt(form.querySelector(".postId").value),
         user: { userId: parseInt(form.querySelector(".userId").value) },
       };
       const redirectPage = form.querySelector(".redirectPage").value;

       const requestBody = {
         posts: posts,
         redirectPage: redirectPage,
       };

       fetch("/like", {
         method: "POST",
         body: JSON.stringify(requestBody),
         headers: {
           "Content-Type": "application/json",
           Accept: "text/html",
         },
       })
         .then((response) => {
           if (response.ok) {
             const contentType = response.headers.get("content-type");
             if (contentType.includes("text/html")) {
               return response.text().then((html) => {
                 const parser = new DOMParser();
                 const doc = parser.parseFromString(html, "text/html");
                 document.body.innerHTML = doc.body.innerHTML;
                 attachLikeFormListeners(); // Reattach listeners after content update
               });
             } else {
               return response.json().then((data) => {
                 alert(data.alert);
               });
             }
           } else {
             throw new Error("Failed to like post");
           }
         })
         .catch((error) => {
           console.error("Error:", error);
           alert("An error occurred while liking the post.");
         });
     });
   });
 }

 document.addEventListener("DOMContentLoaded", attachLikeFormListeners);


//Add Comment
document.addEventListener("DOMContentLoaded", function () {
  const forms = document.querySelectorAll(".addCommentForm"); // Select all like forms

  forms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault(); // Prevent default form submission

      if (!form.checkValidity()) {
        form.classList.add("was-validated");
        return; // Stop if form validation fails
      }

      const formData = new FormData();

      const postComments = {
        post: {
          postId: form.querySelector(".postId").value, // Select fields from current form
        },
        userId: form.querySelector(".userId").value,
        comment: form.querySelector("#comment").value,
      };

      formData.append(
        "postComments",
        new Blob([JSON.stringify(postComments)], { type: "application/json" })
      );

      fetch("/addComment", {
        method: "POST",
        body: formData,
        headers: {
          Accept: "text/html",
        },
      })
        .then((response) => {
          if (response.ok) {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("text/html")) {
              console.log("Comment added successfully.");
              return response.text().then((html) => {
                document.open();
                document.write(html);
                document.close();
              });
            } else {
              return response.json().then((data) => {
                console.log("Comment added successfully.");
                alert(data.alert);
              });
            }
          } else {
            throw new Error("Failed to add comment.");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("An error occurred while adding the comment.");
        });
    });
  });
});

//Delete Comment
document.addEventListener("DOMContentLoaded", function () {
  const forms = document.querySelectorAll(".deleteCommentForm"); // Select all like forms

  forms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault(); // Prevent default form submission

      if (!form.checkValidity()) {
        form.classList.add("was-validated");
        return; // Stop if form validation fails
      }

      const formData = new FormData();

      const postComments = {
        post: {
          postId: form.querySelector(".postId").value, // Select fields from current form
        },
        userId: form.querySelector(".userId").value,
        id: form.querySelector(".commentId").value,
      };

      formData.append(
        "postComments",
        new Blob([JSON.stringify(postComments)], { type: "application/json" })
      );

      fetch("/deleteComment", {
        method: "POST",
        body: formData,
        headers: {
          Accept: "text/html",
        },
      })
        .then((response) => {
          if (response.ok) {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("text/html")) {
              console.log("Comment deleted successfully.");
              return response.text().then((html) => {
                document.open();
                document.write(html);
                document.close();
              });
            } else {
              return response.json().then((data) => {
                console.log("Comment deleted successfully.");
                alert(data.alert);
              });
            }
          } else {
            throw new Error("Failed to delete comment.");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("An error occurred while deleting the comment.");
        });
    });
  });
});

//Edit Comment Form
document.addEventListener("DOMContentLoaded", function () {
  const forms = document.querySelectorAll(".editCommentForm"); // Select all like forms

  forms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault(); // Prevent default form submission

      if (!form.checkValidity()) {
        form.classList.add("was-validated");
        return;
      }

      const formData = new FormData();

      const postComments = {
        post: {
          postId: form.querySelector(".postId").value,
        },
        userId: form.querySelector(".userId").value,
        id: form.querySelector(".commentId").value,
        comment: form.querySelector(".comment").value,
      };

      formData.append(
        "postComments",
        new Blob([JSON.stringify(postComments)], { type: "application/json" })
      );

      fetch("/editComment", {
        method: "POST",
        body: formData,
        headers: {
          Accept: "text/html",
        },
      })
        .then((response) => {
          if (response.ok) {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("text/html")) {
              console.log("Comment page fetched successfully.");
              return response.text().then((html) => {
                document.open();
                document.write(html);
                document.close();
              });
            } else {
              return response.json().then((data) => {
                console.log("Comment page fetched successfully.");
                alert(data.alert);
              });
            }
          } else {
            throw new Error("Failed to fetch comment page.");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("An error occurred while fetching comment page.");
        });
    });
  });
});

//getFollowersRequest
document.addEventListener("DOMContentLoaded", function () {
  const forms = document.querySelectorAll(".followerRequestForm"); // Select all like forms

  forms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault(); // Prevent default form submission

      if (!form.checkValidity()) {
        form.classList.add("was-validated");
        return;
      }

      const formData = new FormData();

      const user = {
        userId: form.querySelector(".userId").value,
      };

      formData.append(
        "user",
        new Blob([JSON.stringify(user)], { type: "application/json" })
      );

      fetch("/getFollowersRequest", {
        method: "POST",
        body: formData,
        headers: {
          Accept: "text/html",
        },
      })
        .then((response) => {
          if (response.ok) {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("text/html")) {
              console.log("Followers request fetched successfully.");
              return response.text().then((html) => {
                document.open();
                document.write(html);
                document.close();
              });
            } else {
              return response.json().then((data) => {
                console.log("Followers request fetched successfully.");
                alert(data.alert);
              });
            }
          } else {
            throw new Error("Failed to fetch Followers request.");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("An error occurred while fetching Followers request.");
        });
    });
  });
});

//Set Following
document.addEventListener("DOMContentLoaded", function () {
  const forms = document.querySelectorAll(".setFollowingForm"); // Select all like forms

  forms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault(); // Prevent default form submission

      if (!form.checkValidity()) {
        form.classList.add("was-validated");
        return;
      }

      const formData = new FormData();

      const following = {
        userId:form.querySelector(".userId").value,
        followerId:form.querySelector(".followerId").value
      };

      formData.append("following",  new Blob([JSON.stringify(following)], { type: "application/json" }));

      fetch("/setFollowing1", {
        method: "POST",
        body: formData,
        headers: {
          Accept: "text/html",
        },
      })
        .then((response) => {
          if (response.ok) {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("text/html")) {
              console.log("Request sent successfully.");
              return response.text().then((html) => {
                document.open();
                document.write(html);
                document.close();
              });
            } else {
              return response.json().then((data) => {
                console.log("Request sent successfully.");
                alert(data.alert);
              });
            }
          } else {
            throw new Error("Failed to sent request.");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("An error occurred while sending request.");
        });
    });
  });
});


//edit comment effect
document.addEventListener("DOMContentLoaded", () => {
  // Add click event listener to all edit buttons
  document.querySelectorAll(".edit-btn").forEach((btn) => {
    btn.addEventListener("click", (event) => {
      const commentId = btn.getAttribute("data-comment-id");
      const commentBox = document.querySelector(
        `[data-comment-box-id="${commentId}"]`
      );

      // Toggle visibility of the comment box
      commentBox.classList.toggle("hidden");
    });
  });
});

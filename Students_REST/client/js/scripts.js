'use strict';

var baseUrl = "http://localhost:8080/";

var studentChange = function (newValue) {
    //console.log("Student change");
    if (newValue.index && newValue.index != 0 && newValue.firstName && newValue.lastName && newValue.birthDate) {
        $.ajax({
            url: baseUrl + "students/" + newValue.index,
            type: 'PUT',
            headers: {'Content-Type': 'application/json; charset=UTF-8'},
            data: JSON.stringify({
                firstName: newValue.firstName,
                lastName: newValue.lastName,
                birthDate: newValue.birthDate
            }),
            success: function (data) {
                console.log("Successfully PUT student", data);
            }
        })
    } else if (newValue.firstName && newValue.lastName && newValue.birthDate) {
        $.ajax({
            url: baseUrl + "students",
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                firstName: newValue.firstName,
                lastName: newValue.lastName,
                birthDate: newValue.birthDate
            }),
            success: function (data) {
                console.log("Received from post", data);
                var student = ko.utils.arrayFirst(appViewModel.students(), function (student) {
                    return student.index() === null && student.firstName() === newValue.firstName && student.lastName() === newValue.lastName && student.birthDate() == newValue.birthDate
                });
                if (student) {
                    student.index(data.index);
                }
            }
        })
    }
};

var courseChange = function (newValue) {
    console.log("Course change", newValue);
    if (newValue.id && newValue.name && newValue.lecturer) {
        $.ajax({
            url: baseUrl + "courses/" + newValue.id,
            type: 'PUT',
            headers: {'Content-Type': 'application/json; charset=UTF-8'},
            data: JSON.stringify({name: newValue.name, lecturer: newValue.lecturer}),
            success: function (data) {
                console.log("Successfully PUT course", data);
            }
        })
    } else if (newValue.name && newValue.lecturer) {
        $.ajax({
            url: baseUrl + "courses",
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({name: newValue.name, lecturer: newValue.lecturer}),
            success: function (data) {
                console.log("Received from post", data);
                var course = ko.utils.arrayFirst(appViewModel.courses(), function (course) {
                    return !course.id && course.name() === newValue.name && course.lecturer() === newValue.lecturer
                });
                if (course) {
                    course.id = data.id;
                }
            }
        })
    }
};

var gradeChange = function (newValue) {
    console.log("Grade change", newValue, "for student", appViewModel.selectedStudent());
    if (newValue.id && newValue.grade && newValue.date && newValue.selectedCourse && newValue.selectedCourse.id) {
        $.ajax({
            url: baseUrl + "students/" + appViewModel.selectedStudent().index() + "/grades/" + newValue.id,
            type: 'PUT',
            headers: {'Content-Type': 'application/json; charset=UTF-8'},
            data: JSON.stringify({grade: newValue.grade, date: newValue.date, course: {id: newValue.selectedCourse.id}}),
            success: function (data) {
                console.log("Successfully PUT grade", data);
            }
        })
    } else if (newValue.grade && newValue.date && newValue.selectedCourse && newValue.selectedCourse.id) {
        $.ajax({
            url: baseUrl + "students/" + appViewModel.selectedStudent().index() + "/grades",
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({grade: newValue.grade, date: newValue.date, course: {id: newValue.selectedCourse.id}}),
            success: function (data) {
                console.log("Received from post", data);
                var grade = ko.utils.arrayFirst(appViewModel.grades(), function (grade) {
                    return !grade.id && grade.date() === newValue.date && grade.grade() === newValue.grade
                });
                if (grade) {
                    grade.id = data.id;
                }
            }
        })
    }
};

var loadGrades = function (student) {
    console.log("Load grades for:", student);
    if(student != null) {
        appViewModel.studentsName(student.firstName() + ' ' + student.lastName());
        var mapping = {
            //key: 'index',
            create: function (options) {
                return new GradeViewModel(options.data)
            }
        };
        $.ajax({
            url: baseUrl + "students/" + student.index() + "/grades",
            type: "GET",
            success: function (data) {
                ko.mapping.fromJS(data, mapping, appViewModel.grades);
                console.log("got grades", data);
            }
        })
    }
};

function StudentViewModel(data) {
    var self = this;
    var mapping = {
        'ignore': ['link']
    };

    ko.mapping.fromJS(data, mapping, self);
    var comp = ko.computed(function () {
        return ko.toJS(self)
    });
    comp.subscribe(studentChange);
    comp.extend({rateLimit: 1000});

}

function GradeViewModel(data) {
    var self = this;
    var mapping = {
        'ignore': ['link'],
        'observe': ['grade', 'date']
    };

    ko.mapping.fromJS(data, mapping, self);
    var comp = ko.computed(function () {
        return ko.toJS(self)
    });
    comp.subscribe(gradeChange);
    comp.extend({rateLimit: 1000});

    self.getCourse = function() {
        //console.log("Gettin course for")
        var courseRef = ko.utils.arrayFirst(data.link, function(link) {
            return link.params.rel === "course";
        }).href;
        //console.log("Course ref:", courseRef);
        var courseId = courseRef.substr(9);
        //console.log("Extracted: ", courseId);
        var course = ko.utils.arrayFirst(appViewModel.courses(), function (course) {
            return course.id  === courseId
        });
        console.log("Found course: ", course);
        return course;
    };
    self.selectedCourse = ko.observable(data.link ? self.getCourse() : null);
    self.selectedCourse.subscribe(function (newValue) {
        gradeChange({grade: self.grade(), date: self.date(), selectedCourse: newValue, id: self.id})
    });

}

function CourseViewModel(data) {
    var self = this;
    var mapping = {
        'ignore': ['link'],
        'observe': ['name', 'lecturer']
    };

    ko.mapping.fromJS(data, mapping, self);
    var comp = ko.computed(function () {
        return ko.toJS(self)
    });
    comp.subscribe(courseChange);
    comp.extend({rateLimit: 1000});
}

function AppViewModel() {
    var self = this;

    self.students = ko.mapping.fromJS([]);
    self.students.subscribe(removeStudent, null, "arrayChange");
    self.courses = ko.mapping.fromJS([]);
    self.courses.subscribe(removeCourse, null, "arrayChange");
    self.grades = ko.mapping.fromJS([]);

    self.selectedStudent = ko.observable(null);
    self.selectedStudent.subscribe(loadGrades);
    self.studentsName = ko.observable(null);

    self.gradesVisible = function() {
        return self.selectedStudent() != null;
    };

    self.loadInitialData = function () {
        createStudents(self);
        createCourses(self);
    };
    self.loadInitialData();


    self.seeStudentGrades = function(student) {
        self.selectedStudent(student);
        return true;
    };

    self.addStudent = function () {
        console.log("Adding student");
        self.students.push(new StudentViewModel({index: null, firstName: "", lastName: "", birthDate: ""}))
    };
    self.removeStudent = function (student) {
        self.students.remove(student);
    };

    self.addCourse = function () {
        console.log("Adding course");
        self.courses.push(new CourseViewModel({name: "", lecturer: ""}))
    };
    self.removeCourse = function (course) {
        self.courses.remove(course);
    };

    self.addGrade = function () {
        console.log("Adding grade");
        self.grades.push(new GradeViewModel({grade: null, date: ""}))
    };
    self.removeGrade = function (grade) {
        console.log("Removing grade", grade);
        if (!grade.id) {
            self.grades.remove(grade);
        } else {
            $.ajax({
                url: baseUrl + "students/" + appViewModel.selectedStudent().index() + "/grades/" + grade.id,
                type: 'DELETE',
                success: function () {
                    self.grades.remove(grade);
                }
            })
        }
    };

    self.courseAfterRender = function(option, item) {
        ko.applyBindingsToNode(option, {disable: !item, visible: !item || (item && item.id)}, item);
        console.log("something", option, item);
    }
}

function removeStudent(changes) {
    changes.forEach(function (change) {
        if(change.status === 'deleted') {
            var student = change.value;
            if (student.index() !== null) {
                console.log("Removing student", student.index());
                $.ajax({
                    url: baseUrl + "students/" + student.index(),
                    type: 'DELETE'
                })
            }
        }
    })
}

function removeCourse(changes) {
    changes.forEach(function (change) {
        if(change.status === 'deleted') {
            var course = change.value;
            if (course.id) {
                console.log("Removing course", course.name());
                $.ajax({
                    url: baseUrl + "courses/" + course.id,
                    type: 'DELETE'
                })
            }
        }
    })
}


var appViewModel = new AppViewModel();

function createStudents() {
    var mapping = {
        //key: 'index',
        create: function (options) {
            return new StudentViewModel(options.data)
        }
    };
    $.ajax({
        url: baseUrl + "students",
        type: "GET",
        success: function (data) {
            ko.mapping.fromJS(data, mapping, appViewModel.students);
        }
    })
}

function createCourses() {
    var mapping = {
        //key: 'index',
        create: function (options) {
            return new CourseViewModel(options.data)
        }
    };
    $.ajax({
        url: baseUrl + "courses",
        type: "GET",
        success: function (data) {
            ko.mapping.fromJS(data, mapping, appViewModel.courses);
        }
    })
}


$(function () {
    ko.applyBindings(appViewModel);
});

/*$(document).ajaxStop(function() {

 });*/


	